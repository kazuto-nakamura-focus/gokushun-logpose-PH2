<template>
  <v-app>
    <v-container class="spacing-playground pa-5" fluid>
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
    </v-container>
  </v-app>
</template>

<script>
import {
  useDeviceList,
  useDeviceInfoRemove,
} from "@/api/ManagementScreenTop/MSDevice";
import MSEditDeviceWrapper from "./MSEditDevice/MSEditDeviceWrapper.vue";
// import MSDeviceData from "@/assets/testData/MSDevice.json"; //
// import MSFieldData from "@/assets/testData/MSField.json"; //

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

      useDeviceInfoDataList: [], //
      selectedDevice : {id:null},
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
  },

  created: function () {},

  methods: {
    clickRow: function (item) {
      this.selectedDevice.id = item.id;
      this.display = "deviceEdit";
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
