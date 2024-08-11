<template>
  <v-app>
    <v-container class="spacing-playground pa-5" fluid>
      <template v-if="display == 'list'">
        <v-app>
          <v-container>
            <v-row>
              <v-col align="left">
                <v-btn
                  color="primary"
                  class="ma-2 white--text"
                  elevation="2"
                  @click="callLoader()"
                  >センサーデータのロード</v-btn
                >
              </v-col>
              <v-col align="right">
                <v-icon
                  v-if="!isDeleteMode"
                  @click="add()"
                  right
                  icon="mdi-vuetify"
                  >mdi-plus</v-icon
                >
                <v-icon
                  v-if="!isDeleteMode"
                  right
                  icon="mdi-vuetify"
                  @click="isDeleteMode = !isDeleteMode"
                  >mdi-trash-can-outline</v-icon
                >
                <v-icon
                  v-if="isDeleteMode"
                  right
                  icon="mdi-vuetify"
                  @click="deleteRow(item)"
                  >mdi-delete-empty</v-icon
                >
                <v-icon
                  v-if="isDeleteMode"
                  right
                  icon="mdi-vuetify"
                  @click="($event) => isDeleteModeTrue()"
                  >mdi-close</v-icon
                >
              </v-col>
            </v-row>
            <v-row>
              <v-col>
                <v-data-table
                  v-if="!isDeleteMode"
                  v-model="selected"
                  :headers="headers"
                  :items="useDeviceInfoDataList"
                  dense
                  class="elevation-1"
                  @click:row="clickRow"
                  mobile-breakpoint="0"
                  :hide-default-footer="false"
                  :page.sync="currentPage"
                  :items-per-page.sync="currentItemsPerPage"
                  @update:page="onPageChange"
                  @update:items-per-page="onItemsPerPageChange"
                ></v-data-table>
                <v-data-table
                  v-if="isDeleteMode"
                  v-model="selected"
                  show-select
                  :headers="headers"
                  :items="useDeviceInfoDataList"
                  dense
                  class="elevation-1"
                  @click:row="clickRow"
                  mobile-breakpoint="0"
                  :hide-default-footer="false"
                  :page.sync="currentPage"
                  :items-per-page.sync="currentItemsPerPage"
                  @update:page="onPageChange"
                  @update:items-per-page="onItemsPerPageChange"
                ></v-data-table>
              </v-col>
            </v-row>
          </v-container>
        </v-app>
      </template>
      <mSEditDeviceWrapper
        v-if="display == 'deviceEdit'"
        :selectedDevice="selectedDevice"
        :onEnd="fromDevice"
        :deviceList="useDeviceInfoDataList"
      />
      <confirmDailog :shared="sharedConfirm" ref="confirm" />
    </v-container>
  </v-app>
</template>

<script>
import {
  useDeviceList,
  useDeviceInfoRemove,
  usePostRequest,
} from "@/api/ManagementScreenTop/MSDevice";
import MSEditDeviceWrapper from "./MSEditDevice/MSEditDeviceWrapper.vue";
import confirmDailog from "@/components-v1/parts/dialog/confirmDialog";
import { DialogController } from "@/lib/mountController.js";

const HEADERS = [
  { text: "デバイス名", value: "name", sortable: true },
  { text: "圃場", value: "field", sortable: true },
  { text: "品種", value: "brand", sortable: true },
  { text: "登録日時", value: "registeredDate", sortable: false },
  { text: "ID", value: "id", sortable: true },
];

export default {
  data() {
    return {
      selected: [],
      headers: HEADERS,
      display: "list",
      isDeleteMode: false,
      sharedConfirm: new DialogController(),
      loaderDialog: new DialogController(),

      useDeviceInfoDataList: [], //
      selectedDevice: { id: null },

      //テーブルの現在ページ・ページ内行数を格納する。
      //ページ・ページ内行数が変更することで、こちらの値を変更する。
      currentPage: 1,
      currentItemsPerPage: 10,
    };
  },

  mounted() {
    //デバイス一覧取得(API)
    useDeviceList()
      .then((response) => {
        //成功時
        const results = response["data"];
        console.log("MSDevice_useDeviceList_results", results);
        this.useDeviceInfoDataList = results.data;
      })
      .catch((error) => {
        //失敗時
        console.log(error);
      });
  },

  components: {
    MSEditDeviceWrapper,
    confirmDailog,
  },

  methods: {
    clickRow: function (item) {
      this.selectedDevice.id = item.id;
      this.display = "deviceEdit";
    },
    // ======================================================
    // ロードボタン選択時
    // ======================================================
    callLoader: function () {
      usePostRequest(-1)
        .then((response) => {
          const { status, message } = response["data"];
          if (status == 0) {
            alert(
              "更新リクエストを受付ました。結果は左のメニューから確認してください。"
            );
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
          alert("更新リクエストに失敗しました。");
          console.log(error);
        });
    },

    add: function () {
      this.selectedDevice.id = null;
      this.display = "deviceEdit";
    },

    isDeleteModeTrue: function () {
      this.isDeleteMode = false;
    },
    deleteRow: function () {
      if (this.selected.length >= 1) {
        if (confirm("Are you sure you want to delete this item?")) {
          for (var i = 0; i < this.selected.length; i++) {
            console.log("selectd[i]", this.selected[i].id);
            const id = this.selected[i].id;

            //DELETE例
            //デバイス情報削除(API)
            useDeviceInfoRemove(parseInt(id))
              .then((response) => {
                //成功時
                const results = response["data"];
                console.log(results);
              })
              .catch((error) => {
                //失敗時
                console.log(error);
              });

            const index = this.useDeviceInfoDataList.indexOf(this.selected[i]);
            this.useDeviceInfoDataList.splice(index, 1);
          }
          this.selected = [];
        }
        this.isDeleteMode = false;
      }
    },
    fromDevice: function (isChanged) {
      if (isChanged) {
        this.useDeviceInfoDataList.length = 0;
        //デバイス一覧習得(API)
        useDeviceList()
          .then((response) => {
            //成功時
            const results = response["data"];
            console.log("MSDevice_useDeviceList_results", results);
            this.useDeviceInfoDataList = results.data;
            this.display = "list";
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          });
      } else {
        this.display = "list";
      }
    },
    // ページ変更時のページ番号を一時的に記憶する。
    onPageChange: function (newPage) {
      this.currentPage = newPage;
    },
    // 行数変更時の行数を一時的に記憶する。
    onItemsPerPageChange: function (newItemsPerPage) {
      this.currentItemsPerPage = newItemsPerPage;
    },
  },
};
</script>

<style lang="scss"></style>
