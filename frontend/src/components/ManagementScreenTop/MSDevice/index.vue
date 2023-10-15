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
                  @click="load()"
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
                >
                </v-data-table>
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
                >
                </v-data-table>
              </v-col>
            </v-row>
          </v-container>
        </v-app>
      </template>
      <mSEditDeviceWrapper
        v-if="display == 'deviceEdit'"
        :selectedDevice="selectedDevice"
        :onEnd="fromDevice"
      />
      <confirmDailog :shared="sharedConfirm" ref="confirm" />
    </v-container>
  </v-app>
</template>

<script>
import {
  useDeviceList,
  useDeviceInfoRemove,
  useLoadData,
} from "@/api/ManagementScreenTop/MSDevice";
import MSEditDeviceWrapper from "./MSEditDevice/MSEditDeviceWrapper.vue";
import confirmDailog from "@/components/dialog/confirmDialog.vue";
import { DialogController } from "@/lib/mountController.js";

const HEADERS = [
  { text: "デバイス名", value: "name", sortable: true },
  { text: "圃場", value: "field", sortable: true },
  { text: "品種", value: "brand", sortable: true },
  { text: "登録日時", value: "registeredDate", sortable: false },
];

export default {
  data() {
    return {
      selected: [],
      headers: HEADERS,
      display: "list",
      isDeleteMode: false,
      sharedConfirm: new DialogController(),

      useDeviceInfoDataList: [], //
      selectedDevice: { id: null },
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

  created: function () {},

  methods: {
    clickRow: function (item) {
      this.selectedDevice.id = item.id;
      this.display = "deviceEdit";
    },
    // ======================================================
    // ボタン選択時
    // ======================================================
    load: function () {
      this.sharedConfirm.setUp(
        this.$refs.confirm,
        function (confirm) {
          confirm.initialize(
            "全てのセンサーデータが変更されますが、よろしいですか？処理時間は１時間ほどかかります。",
            "はい",
            "いいえ"
          );
        },
        function (action) {
          if (action) {
            this.callLoader();
          }
        }.bind(this)
      );
    },
    callLoader: function () {
      const data = {
        deviceId: null,
        isAll: true,
        startDate: null,
      };
      useLoadData(data)
        .then((response) => {
          //成功時
          const { status, message } = response["data"];
          if (status === 0) {
            alert("センサーデータのロードが完了しました。");
            this.onEnd(true);
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
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
  },
};
</script>

<style lang="scss"></style>
