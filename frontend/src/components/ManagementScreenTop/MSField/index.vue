<template>
  <v-app>
    <v-container class="spacing-playground" fluid>
      <template v-if="display == 'list'">
        <v-app>
          <v-container>
            <v-row>
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
                  @click="deleteField"
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
                <v-sheet
                  id="deviceListMap"
                  class="pa-0"
                  :light="true"
                  style="width: 100%; height: 300px"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col>
                <v-data-table
                  v-if="!isDeleteMode"
                  v-model="selected"
                  item-key="name"
                  :headers="headers"
                  :items="useFieldInfoDataList"
                  dense
                  class="elevation-1"
                  @click:row="clickRow"
                  mobile-breakpoint="0"
                  :hide-default-footer="false"
                  :page.sync="currentPage"
                  :items-per-page.sync="currentItemsPerPage"
                  @update:page="onPageChange"
                  @update:items-per-page="onItemsPerPageChange"
                >
                </v-data-table>
                <v-data-table
                  v-if="isDeleteMode"
                  v-model="selected"
                  show-select
                  item-key="name"
                  :headers="headers"
                  :items="useFieldInfoDataList"
                  dense
                  class="elevation-1"
                  @click:row="clickRow"
                  mobile-breakpoint="0"
                  :hide-default-footer="false"
                  :page.sync="currentPage"
                  :items-per-page.sync="currentItemsPerPage"
                  @update:page="onPageChange"
                  @update:items-per-page="onItemsPerPageChange"
                >
                </v-data-table>
              </v-col>
            </v-row>
            <v-row>
              <v-card-text style="height: 50px; position: relative">
                <v-fab-transition>
                  <v-btn
                    @click="add()"
                    v-show="!hidden"
                    color="primary"
                    dark
                    absolute
                    top
                    right
                    fab
                  >
                    <v-icon>+</v-icon>
                  </v-btn>
                </v-fab-transition>
              </v-card-text>
            </v-row>
          </v-container>
        </v-app>
      </template>
      <MSEdit
        v-else-if="display == 'fieldEdit'"
        :onBackPressed="handleBack"
        :onUpdate="handleUpdate"
        :onDelete="handleDelete"
        :useFieldInfoData="useFieldInfoData"
        :onDeviceEdit="deviceEdit"
        @handleDeviceDetailInfo="handleDeviceDetailInfo"
        @refreshFieldList="refreshFieldList"
      />
      <mSEditDeviceWrapper
        v-if="display == 'editDevice'"
        :selectedDevice="selectedDevice"
        :onEnd="handleBack"
      />
    </v-container>
  </v-app>
</template>

<script>
import { Loader } from "@googlemaps/js-api-loader";
import { useFieldList } from "@/api/ManagementScreenTop/MSField";
import { useFieldInfo } from "@/api/ManagementScreenTop/MSField";
import { useFieldInfoRemove } from "@/api/ManagementScreenTop/MSField";
import MSEdit from "./MSEdit.vue";
import moment from "moment";
import mSEditDeviceWrapper from "@/components/ManagementScreenTop/MSDevice/MSEditDevice/MSEditDeviceWrapper.vue";
import { mapLoaderOptions, mapOptions } from "./mapConfig";

const HEADERS = [
  { text: "圃場名", value: "name", sortable: true },
  { text: "場所", value: "location", sortable: true },
  { text: "取引先", value: "contructor", sortable: true },
  { text: "登録日時", value: "registeredDate", sortable: false },
];

export default {
  data() {
    return {
      hidden: false,
      selected: [],
      headers: HEADERS,
      display: "list",
      isDeleteMode: false,

      useFieldInfoData: null,
      useFieldInfoDataList: [],
      selectedDevice: { id: null },

      mapLoader: null,
      map: null,

      //テーブルの現在ページ・ページ内行数を格納する。
      //ページ・ページ内行数が変更することで、こちらの値を変更する。
      currentPage: 1,
      currentItemsPerPage: 10,
    };
  },

  mounted() {
    //マップ描画の初期設定
    this.mapLoader = new Loader(mapLoaderOptions);
    this.mapLoader
      .load()
      .then((google) => {
        this.google = google;
        // 地図の初期化
        this.map = new google.maps.Map(document.getElementById("deviceListMap"), {
          // 初期表示設定
          ...mapOptions,
          //フォーカスを充てる座標指定
          center: { lat: 35.692195, lng: 139.759854 }, // マルティスープ本社
        });
      })
      .catch((e) => {
        console.error(e);
      });

    //圃場一覧取得(API)
    useFieldList()
      .then((response) => {
        //成功時
        const { status, message, data } = response["data"];
        if (status === 0) {
          this.useFieldInfoDataList = data;
        } else {
          throw new Error(message);
        }
      })
      .catch((error) => {
        //失敗時
        console.log(error);
      });
  },

  updated() {
    //画面の再描画
    this.mapLoader
      .load()
      .then((google) => {
        this.google = google;
        // 地図の初期化
        this.map = new google.maps.Map(document.getElementById("deviceListMap"), {
          // 初期表示設定
          ...mapOptions,
          //フォーカスを充てる座標指定
          center: { lat: 35.692195, lng: 139.759854 }, // マルティスープ本社
        });
      })
      .catch((e) => {
        console.error(e);
      });
  },

  components: {
    MSEdit,
    mSEditDeviceWrapper,
  },

  methods: {
    handleDeviceDetailInfo: function (data) {
      this.selectedDevice.id = data.id;
    },

    clickRow: function (item) {
      this.useFieldInfoData = item;

      //圃場情報詳細取得(API)
      useFieldInfo(this.useFieldInfoData.id)
        .then((response) => {
          //成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            this.useFieldInfoData = data;
            this.display = "fieldEdit";
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
      this.updateData = null;
      this.useFieldInfoData = {
        id: null,
        name: "",
        location: "",
        latitude: "",
        longitude: "",
        contructor: "",
        registeredDate: moment().format("YYYY-MM-DD"),
        deviceList: [],
      };
      this.display = "fieldEdit";
    },
    handleBack: function () {
      this.display = "list";
    },
    handleAdd: function () {
      this.display = "list";
    },
    handleUpdate: function () {
      this.display = "list";
    },
    handleDelete: function () {
      this.display = "list";
    },
    deviceEdit: function () {
      this.display = "editDevice";
    },
    isDeleteModeTrue: function () {
      this.isDeleteMode = false;
    },
    deleteField: function () {
      if (this.selected.length >= 1) {
        if (confirm("削除してもよろしいですか？")) {
          for (var i = 0; i < this.selected.length; i++) {
            console.log("selected[i]", this.selected[i].id);
            const id = this.selected[i].id;

            //DELETE例
            //圃場削除(API)
            useFieldInfoRemove(parseInt(id))
              .then((response) => {
                //成功時
                const { status, message } = response["data"];
                if (status === 0) {
                  alert("削除に成功しました。");
                  const index = this.useFieldInfoDataList.indexOf(
                    this.selected[i]
                  );
                  this.useFieldInfoDataList.splice(index, 1);
                } else {
                  throw new Error(message);
                }
              })
              .catch((error) => {
                //失敗時
                console.log(error);
                alert("削除に失敗しました。");
              });
          }
          this.selected = [];
        }
        this.isDeleteMode = false;
      }
    },
    refreshFieldList: function () {
      console.log("called_refreshFieldList");
      //圃場一覧取得(API)
      useFieldList()
        .then((response) => {
          //成功時
          const { status, message, data } = response["data"];
          if (status === 0) {
            this.useFieldInfoDataList = data;
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    
    // ページ変更時のページ番号を一時的に記憶する。
    onPageChange: function(newPage) {
      this.currentPage = newPage;
    },
    // 行数変更時の行数を一時的に記憶する。
    onItemsPerPageChange: function(newItemsPerPage) {
      this.currentItemsPerPage = newItemsPerPage;
    }
  },
};
</script>

<style lang="scss">
// ヘッダー部
.fields {
  display: flex;
  padding: 3pt;
  font-size: 11pt;
  width: 90%;
  flex-wrap: wrap;
}

.menu {
  width: 90%;
  display: flex;
  justify-content: space-strech;
  flex-wrap: wrap;
}

.date {
  //display: flex;
  font-size: 11pt;
  // width: 80%;
  //justify-content: space-strech;
  //flex-wrap: wrap;
}
</style>
