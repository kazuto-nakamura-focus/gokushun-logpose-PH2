<template>
  <v-app>
    <v-container>
      <template>
        <v-row v-if="mode == 'update'">
          <!-- 更新モードの時表示-->
          <v-col align="right">
            <v-icon right icon="mdi-vuetify" @click="deleteDeviceInfo()"
              >mdi-trash-can-outline</v-icon
            >
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="2">
            <div>デバイス名</div>

            <v-text-field
              dense
              hide-details="auto"
              outlined
              v-model="deviceInfoData.name"
            ></v-text-field>

            <p></p>

            <div>圃場</div>

            <v-select
              v-bind:items="useFieldInfoDataList"
              dense
              outlined
              @change="setField"
              v-model="deviceInfoData.fieldId"
              item-text="name"
              item-value="id"
              return-object
            ></v-select>

            <div>品種</div>

            <v-text-field
              dense
              hide-details="auto"
              outlined
              v-model="deviceInfoData.brand"
            ></v-text-field>

            <p></p>

            <div>Sigfox Device ID</div>

            <v-text-field
              dense
              hide-details="auto"
              outlined
              v-model="deviceInfoData.sigFoxDeviceId"
            ></v-text-field>

            <p></p>

            <div>基準日</div>

            <v-text-field
              dense
              hide-details="auto"
              outlined
              placeholder="01/01"
              v-model="deviceInfoData.baseDateShort"
            ></v-text-field>

            <div>タイムゾーン</div>
            <!--<v-select
              v-model="deviceInfoData.timeZone"
              :items="timeZone"
              width="60"
              item-text="name"
              item-value="id"
              dense
              return-object
            ></v-select>-->
            <v-select
              v-model="deviceInfoData.timeZone"
              :items="timeZone"
              width="60"
              item-text="name"
              item-value="id"
              dense
            ></v-select>
          </v-col>

          <v-col cols="10">
            <div>センサー</div>

            <div style="height: 325px">
              <div style="height: 325px; box-sizing: border-box">
                <AgGridVue
                  style="width: 100%; height: 100%"
                  class="ag-theme-gs"
                  :columnDefs="columnDefs"
                  @grid-ready="onGridReady"
                  :rowData="rowData"
                  :gridOptions="gridOptions"
                  sizeColumn
                  @cell-clicked="onCellClicked"
                  @cell-value-changed="onColumnValueChanged"
                >
                </AgGridVue>
              </div>
            </div>
          </v-col>
        </v-row>

        <v-card-actions>
          <v-spacer></v-spacer>

          <div class="GS_ButtonArea">
            <v-btn
              color="primary"
              class="ma-2 white--text"
              elevation="2"
              @click="update()"
              >{{ label }}</v-btn
            >
            <v-btn
              v-if="deviceInfoData.id != null"
              color="primary"
              class="ma-2 white--text"
              elevation="2"
              @click="dataLoad()"
              >センサーデータのロード</v-btn
            >

            <v-btn
              color="gray"
              class="ma-2 black--text"
              elevation="2"
              @click="back()"
              >キャンセル</v-btn
            >
          </div>
        </v-card-actions>
      </template>
    </v-container>
    <wait-dialog ref="wait" />
  </v-app>
</template>

<script>
import {
  useDeviceInfoAdd,
  useDeviceInfoUpdate, //* 更新モードの時使用
  useDeviceInfoRemove,
  useLoadData,
} from "@/api/ManagementScreenTop/MSDevice";
import { AgGridVue } from "ag-grid-vue";
import moment from "moment-timezone";
import "moment/locale/ja";
import WaitDialog from "@/components/dialog/WaitDialog.vue";

function RemoveCellRenderer() {
  let eGui = document.createElement("div");
  eGui.innerHTML = `
  <div>
  <button data-action="remove" >-</button>
  </div>`;
  return eGui;
}

function AddCellRenderer() {
  let eGui = document.createElement("div");
  eGui.innerHTML = `
  <div>
  <button data-action="add" >+</button>
  </div>`;
  return eGui;
}

export default {
  props: {
    mode: {
      type: String,
      required: true,
    },
    //* 更新モードの時使用
    onEnd: {
      type: Function,
      required: false,
    },
    useFieldInfoDataList: Array, //* 圃場一覧
    useDeviceMasters: Object, //* マスター情報一覧
    useDeviceInfoData: Object, //デバイス詳細
  },

  data() {
    return {
      timeZone: [],
      label: this.mode == "update" ? "更新" : "追加",
      columnDefs: [
        {
          field: "displayId",
          headerName: "センサータイプ",
          singleClickEdit: true,
          resizable: true,
          editable: true,
          width: 125,
          cellEditor: "agSelectCellEditor",
          cellEditorParams: {
            values: this.extractKeys(
              this.$options.propsData.useDeviceMasters.sensorContents
            ),
          },
          refData: this.useDeviceMasters.sensorContents,
          valueListGap: 0,
        },
        {
          field: "modelId",
          headerName: "センサー型番",
          singleClickEdit: true,
          resizable: true,
          editable: true,
          width: 150,
          cellEditor: "agSelectCellEditor",
          cellEditorParams: {
            values: this.extractKeys(
              this.$options.propsData.useDeviceMasters.sensorModels
            ),
          },
          refData: this.useDeviceMasters.sensorModels,
          valueListGap: 0,
        },
        {
          field: "channel",
          headerName: "チャンネル",
          singleClickEdit: true,
          resizable: true,
          editable: true,
          width: 80,
          cellEditor: "agSelectCellEditor",
          cellEditorParams: {
            values: [
              "1",
              "2",
              "3",
              "4",
              "5",
              "6",
              "7",
              "8",
              "9",
              "10",
              "11",
              "12",
              "13",
              "14",
              "15",
              "16",
            ],
            valueListGap: 0,
          },
        },
        {
          field: "name",
          singleClickEdit: true,
          headerName: "センサ―名",
          editable: true,
          resizable: true,
          width: 100,
        },
        {
          field: "sizeId",
          singleClickEdit: true,
          headerName: "サイズ",
          resizable: true,
          editable: true,
          width: 100,
          cellEditor: "agSelectCellEditor",
          cellEditorParams: {
            values: this.extractKeys(
              this.$options.propsData.useDeviceMasters.sensorSizes
            ),
          },
          refData: this.useDeviceMasters.sensorSizes,
          valueListGap: 0,
        },
        {
          field: "kst",
          singleClickEdit: true,
          colId: "kst",
          headerName: "Kst",
          resizable: true,
          width: 80,
          cellClass: "originalClass",
          editable: (params) =>
            params.data.displayId == "4" || params.data.displayId == null,
          cellStyle: (params) => {
            if (
              !(params.data.displayId == null || params.data.displayId == "4")
            ) {
              return { backgroundColor: "#aaa" };
            }
            //  return null;
          },
        },
        {
          field: "stemDiameter",
          singleClickEdit: true,
          colId: "stemDiameter",
          headerName: "茎径(mm)",
          resizable: true,
          width: 80,
          editable: (params) =>
            params.data.displayId == "4" || params.data.displayId == null,
          cellStyle: (params) => {
            if (
              !(params.data.displayId == null || params.data.displayId == "4")
            ) {
              return { backgroundColor: "#aaa" };
            }
            //  return null;
          },
        },
        {
          field: "remove",
          headerName: "削除",
          cellRenderer: RemoveCellRenderer,
          colId: "remove",
          editable: false,
          width: 75,
        },
        {
          field: "add",
          headerName: "追加",
          cellRenderer: AddCellRenderer,
          colId: "add",
          editable: false,
          width: 75,
        },
      ],
      rowData: [],
      gridOptions: {
        // 列の定義
        columnDefs: this.columnDefs,
      },
      selections: null,
      skelton: {
        id: null,
        name: null,
        channel: null,
        modelId: null,
        modelName: null,
        displayId: null,
        displayName: null,
        stemDiameter: null,
        kst: null,
        sizeId: null,
        size: null,
      },
      deviceInfoData:
        null != this.useDeviceInfoData
          ? this.useDeviceInfoData
          : Object.assign({}, this.skelton),
      sensorList: null,
      // sensorData: null, //マスターセンサーデータ
    };
  },

  components: {
    AgGridVue,
    WaitDialog,
  },

  mounted() {
    let txList = moment.tz.names();
    for (const item of txList) {
      this.timeZone.push({
        name: item,
        id: item,
      });
    }
  },
  methods: {
    extractKeys(mappings) {
      var value = Object.keys(mappings);
      return value;
    },
    //センサーマスターデータをcellEditorParamsに格納
    getValues() {
      if (this.mode == "update") {
        this.sensorList = this.useDeviceInfoData.sensorItems;
      } else {
        this.sensorList = [];
      }
      return { values: this.sensorList };
    },

    setField(item) {
      this.deviceInfoData.fieldId = item.id;
    },
    //* ============================================
    // セルの値が変化した場合
    //* ============================================
    onColumnValueChanged: function (param) {
      //* 樹液流の場合
      if ("4" != param.data.displayId) {
        console.log("***");
        console.log(this.gridOptions.columnDefs[5]);
        this.gridOptions.columnDefs[5].cellStyle = {
          "background-color": "#aaa",
        };
        this.gridOptions.api.refreshCells();
      }
      /*param.data.kst.editable = (4 == param.data.displayId);
      param.data.kst.cellStyle = (4 == param.data.displayI) ? null: "#aaa";
      param.data.stemDiameter.editable = 4 == param.data.displayId;
      param.data.stemDiameter.cellStyle = (4 == param.data.displayI) ? null: "#aaa"*/
    },
    update: function () {
      const message =
        this.mode == "update"
          ? "更新してもよろしいですか？"
          : "登録してもよろしいですか？";
      const deviceId = this.mode == "update" ? this.deviceInfoData.id : null;
      if (confirm(message)) {
        console.log("updateRowData", this.rowData);
        // センサー情報を追加する
        let sensorItems = [];
        // 画面のセンサー情報のリストを取り出す
        for (const item of this.rowData) {
          if (
            null != item.displayId &&
            null != item.name &&
            null != item.channel
          ) {
            sensorItems.push(item);
          }
        }
        const data = {
          //デバイス情報
          id: deviceId,
          name: this.deviceInfoData.name,
          fieldId: this.deviceInfoData.fieldId,
          brand: this.deviceInfoData.brand,
          sigFoxDeviceId: this.deviceInfoData.sigFoxDeviceId,
          baseDateShort: this.deviceInfoData.baseDateShort,
          timeZone: this.deviceInfoData.timeZone,
          //センサー情報
          sensorItems: sensorItems,
        };

        console.log("update_data", data);

        if (this.mode == "update") {
          //デバイス情報更新(API)
          useDeviceInfoUpdate(data)
            .then((response) => {
              //成功時
              const { status, message } = response["data"];
              if (status === 0) {
                alert("更新を成功しました。");
                this.onEnd(true);
              } else {
                throw new Error(message);
              }
            })
            .catch((error) => {
              //失敗時
              console.log(error);
              alert("更新を失敗しました。");
            });
        } else {
          useDeviceInfoAdd(data)
            .then((response) => {
              //成功時
              const { status, message } = response["data"];
              if (status === 0) {
                alert("登録を成功しました。");
                this.onEnd(true);
              } else {
                throw new Error(message);
              }
            })
            .catch((error) => {
              //失敗時
              console.log(error);
              alert("登録を失敗しました。");
            });
        }
      }
    },

    dataLoad: function () {
      const data = {
        deviceId: this.deviceInfoData.id,
        isAll: true,
        startDate: null,
      };
      this.$refs.wait.start("センサーデータをアップデート中です。", true);
      useLoadData(data)
        .then((response) => {
          //成功時
          const { status, message } = response["data"];
          if (status === 0) {
            alert("センサーデータのロードが完了しました。");
            this.$refs.wait.finish();
            this.onEnd(true);
          } else {
            throw new Error(message);
          }
        })
        .catch((error) => {
          //失敗時
          alert("センサーデータのロードが失敗しました。");
          this.$refs.wait.finish();
          console.log(error);
        });
    },
    deleteDeviceInfo: function () {
      if (confirm("削除してもよろしいですか？")) {
        //デバイス情報削除(API)
        console.log("deleteDeviceInfo", this.deviceInfoData.id);
        useDeviceInfoRemove(this.deviceInfoData.id)
          .then((response) => {
            //成功時
            const { status, message } = response["data"];
            if (status === 0) {
              this.onEnd(true);
            } else {
              throw new Error(message);
            }
          })
          .catch((error) => {
            //失敗時
            console.log(error);
          });
      }
    },
    back: function () {
      this.onEnd(false);
    },
    //gridApi使用設定
    onGridReady: function (params) {
      this.gridApi = params.api;
      this.gridColumnApi = params.columnAPI;
      var row = Object.assign({}, this.skelton);
      this.rowData = [row];

      if (this.useDeviceInfoData.sensorItems.length === 0) {
        this.gridApi.forEachNode((node) => this.rowData.push(node.data));
        const rowDataArray = [...this.rowData];
        console.log("rowDataArray", rowDataArray);
      } else {
        this.rowData = [...this.useDeviceInfoData.sensorItems];
        console.log("MSEditDevice_rowData(3):", this.rowData);
      }
    },

    //row追加・削除
    onCellClicked(params) {
      if (params.column.colId === "remove") {
        this.rowData = this.rowData.filter((item) => item.id != params.data.id);
        params.api.applyTransaction({
          remove: [params.node.data],
        });
      }
      if (params.column.colId === "add") {
        var row = Object.assign({}, this.skelton);
        this.rowData.push(row);
        params.api.applyTransaction({
          add: [row],
        });
      }
    },
  },
};
</script>
<style scoped>
.originalClass {
}
.unactivateClass {
  background-color: "#aaa";
}
</style>