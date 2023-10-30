<template>
  <v-app>
    <v-container class="spacing-playground pa-5" fluid>
      <targetMenu
        ref="targetMenu"
        :shared="sharedMenu"
        :model="isModel"
      ></targetMenu>
      <v-expand-transition>
        <div v-if="bodyStatus">
          <template>
            <v-container>
              <v-row class="ma-1">
                <v-col cols="3">
                  <v-menu
                    v-model="menu1"
                    :close-on-content-click="false"
                    :nudge-right="40"
                    transition="scale-transition"
                    offset-y
                    min-width="auto"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-text-field
                        dense
                        hide-details="auto"
                        outlined
                        v-bind="attrs"
                        v-on="on"
                        v-model="startDate"
                        append-icon="mdi-calendar-blank"
                      >
                      </v-text-field>
                    </template>
                    <v-date-picker
                      v-model="startDate"
                      @input="menu1 = false"
                      @change="handleChangeDate()"
                    >
                    </v-date-picker>
                  </v-menu>
                </v-col>
                <div class="mt-5">&nbsp;～&nbsp;</div>
                <v-col cols="3">
                  <v-menu
                    v-model="menu2"
                    :close-on-content-click="false"
                    :nudge-right="40"
                    transition="scale-transition"
                    offset-y
                    min-width="auto"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-text-field
                        dense
                        hide-details="auto"
                        outlined
                        v-bind="attrs"
                        v-on="on"
                        v-model="endDate"
                        append-icon="mdi-calendar-blank"
                      >
                      </v-text-field>
                    </template>
                    <v-date-picker
                      v-model="endDate"
                      @input="menu2 = false"
                      @change="handleChangeDate()"
                    >
                    </v-date-picker>
                  </v-menu>
                </v-col>
              </v-row>
            </v-container>
          </template>
        </div>
      </v-expand-transition>

      <!-- データテーブル -->
      <div v-if="bodyStatus">
        <template>
          <v-container>
            <v-row>
              <v-col cols="12">
                <div class="view_data_table">
                  <v-data-table
                    :headers="masterRawDataHeader"
                    :items="masterRawData"
                    :items-per-page="-1"
                    height="500"
                    fixed-header
                    dense
                    hide-default-footer
                  />
                </div>
              </v-col>
            </v-row>
            <v-row class="pt-1 pr-2 justify-end">
              <v-btn color="primary" outlined @click="handleDownloadCSV()">
                Download
              </v-btn>
            </v-row>
          </v-container>
        </template>
      </div>
    </v-container>
  </v-app>
</template>

<script>
import moment from "moment";
import targetMenu from "@/components/parts/Ph2TargetMenu.vue";
import { MountController } from "@/lib/mountController.js";
import { useRawData } from "@/api/Top";

//----------------------------------------------------------------------
/**
 * APIのインポート方法
 * ※index.jsで記述した内容は、ファイル名まで指定ではなくフォルダ名で指定する
 */
//GET方式の例
//クエリなし
// import { useModels } from "@/api/GrowthEstimationGraph";
//クエリあり
// import { useGrowthFAll } from "@/api/TopStateGrowth/GEFValue";

//POSTやPUT方式の例
// import { useLeafValueAreaAndCount } from "@/api/TopStateGrowth/LAActualValueInput";

//DELETE方式の例
// import { useFruitParamSetRemove } from "@/api/TopStateGrowth/FVParameterSets";
//----------------------------------------------------------------------

export default {
  data() {
    return {
      isModel: false,
      sharedMenu: new MountController(),
      bodyStatus: false,

      menu1: false,
      menu2: false,
      masterRawDataHeader: [],
      headerMap: {
        1: "温度(℃)",
        2: "湿度(％RH)",
        3: "日射(W/㎡)",
        4: "樹液流(g/h)",
        5: "デンドロ(μm)",
        6: "葉面濡れ(raw counts)",
        7: "土壌水分(pF)",
        8: "土壌温度(℃)",
      },
      masterRawData: [],

      // 選択データリスト
      selectedField: null,
      selectedDevice: null,
      selectedItems: [],
      selectedItem: {
        field: String,
        device: String,
      },
      startDate: moment().startOf("months").format("YYYY-MM-DD"),
      endDate: moment().endOf("months").format("YYYY-MM-DD"),

      PRIVATE: {
        getValue(item) {
          return "-" == item ? "-" : Math.floor(item * 100) / 100;
        },
      },
    };
  },
  mounted() {
    this.sharedMenu.setUp(
      this.$refs.targetMenu,
      function (menu) {
        menu.initialize();
      },
      function (status, selected) {
        this.bodyStatus = status;
        if (this.bodyStatus) {
          // 選択されたボタンの内容を取得
          this.selectedItems = selected;
          // データの表示
          this.setListData();
        }
      }.bind(this)
    );
  },
  updated() {},
  components: {
    targetMenu,
  },
  created: function () {},
  methods: {
    setListData: function () {
      this.masterRawData.length = 0;
      this.masterRawDataHeader.length = 0;

      useRawData(
        this.selectedItems.selectedDevice.id,
        this.startDate,
        this.endDate
      )
        .then((response) => {
          var rawData = response.data.data;
          //* ヘッダーの生成
          const header = rawData.headers;
          let i = 0;
          this.masterRawDataHeader.push({
            text: "日時",
            value: "col" + (i++),
            align: "center",
            sortable: false,
          });

          for (const id of header) {
            const item = {
              text: this.headerMap[id],
              value: "col" + (i++),
              align: "center",
              sortable: false,
            };
            this.masterRawDataHeader.push(item);
          }
          console.log(this.masterRawDataHeader);
          console.log(rawData.data);
          for (const items of rawData.data) {
            let value = {};
            let i = 0;
            for (const item of items){
              let itemValue =item;
              if(i != 0 ){
                itemValue = this.PRIVATE.getValue(itemValue);
              }
              value["col" + i] = new Object;
              value["col" + i] = itemValue;
              i++;
            }
            console.log(value);
            this.masterRawData.push(value);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },

    handleChangeDate: function () {
      // データ作成
      this.setListData();
    },
    // CSVダウンロード処理
    handleDownloadCSV: function () {
      const arrayHeaders = [];
      const arrayDatas = [];
      console.log(this.masterRawDataHeader.length);
      this.masterRawDataHeader.map((headerItem) => {
        arrayHeaders.push(headerItem.text);
      });
      arrayDatas.push(arrayHeaders);
      this.masterRawData.map((item) => {
        const data = [
          item.col0,
          item.col1,
          item.col2,
          item.col3,
          item.col4,
          item.col5,
          item.col6,
          item.col7,
          item.col8,
          item.col9,
          item.col10,
          item.col11,
        ];
        arrayDatas.push(data);
      });
      console.log("DownloadData", arrayDatas);

      const date = moment().format("YYYYMMDD_HHmmss");
      const fileName = "download_" + date + ".csv";
      // 改行コード追加
      const csvData = arrayDatas.map((data) => data.join(",")).join("\r\n");
      const bom = new Uint8Array([0xef, 0xbb, 0xbf]);
      const blob = new Blob([bom, csvData], { type: "text/csv" });
      const url = (window.URL || window.webkitURL).createObjectURL(blob);
      const download = document.createElement("a");
      download.href = url;
      download.download = fileName;
      download.click();
      (window.URL || window.webkitURL).revokeObjectURL(url);
    },
  },
};
</script>

<style lang="scss">
.view_data_table {
  border: solid 1px;
}

// .fields {
//   display: flex;
//   padding: 3pt;
//   font-size: 11pt;
//   width: 90%;
//   flex-wrap: wrap;
// }

// .menu {
//   width: 90%;
//   display: flex;
//   justify-content: space-strech;
//   flex-wrap: wrap;
// }

// .date {
//   //display: flex;
//   font-size: 11pt;
//   // width: 80%;
//   //justify-content: space-strech;
//   //flex-wrap: wrap;
// }
</style>
