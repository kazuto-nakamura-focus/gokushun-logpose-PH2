<!--F値テーブル参照画面案 -->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="720" height="700">
        <v-card>
          <v-container>
            <toggle-button
              :value="false"
              v-model="isEditMode"
              change="onChangeEventHandler"
              :width="125"
              :height="27"
              :font-size="12"
              :labels="{ checked: '編集モード', unchecked: '表示モード' }"
            ></toggle-button>
            <v-container>
              <v-row no-gutters>
                <v-col :cols="4">
                  <v-card-text>
                    圃場名<br />
                    <p class="font-weight-bold">{{ fieldName }}</p>
                  </v-card-text>
                </v-col>
                <v-col>
                  <v-card-text>
                    デバイス名<br />
                    <p class="font-weight-bold">{{ deviceName }}</p>
                  </v-card-text>
                </v-col>
              </v-row>
              <div style="height: 250px">
                <div style="height: 250px; box-sizing: border-box">
                  <AgGridVue
                    style="width: 100%; height: 100%; font-size: 9pt"
                    class="ag-theme-gs"
                    sizeColumn
                    :columnDefs="columnDefs"
                    :rowData="rowData"
                    @grid-ready="onGridReady"
                    :gridOptions="gridOptions"
                    :defaultColDef="defaultColDef"
                    @cell-clicked="onButtonClicked"
                    @cellValueChanged="onColumnValueChanged"
                  >
                  </AgGridVue>
                </div>
              </div>
            </v-container>
            <v-card-actions>
              <v-spacer></v-spacer>
              <div v-if="!isEditMode" class="GS_ButtonArea">
                <v-btn
                  color="gray"
                  class="ma-2 black--text"
                  elevation="2"
                  @click="close"
                  >キャンセル</v-btn
                >
              </div>
              <div v-if="isEditMode" class="GS_ButtonArea">
                <!-- <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="reset">戻る</v-btn> -->
                <v-btn
                  color="primary"
                  class="ma-2 white--text"
                  elevation="2"
                  @click="save"
                  >保存</v-btn
                >
                <v-btn
                  color="gray"
                  class="ma-2 black--text"
                  elevation="2"
                  @click="close"
                  >キャンセル</v-btn
                >
              </div>
            </v-card-actions>
          </v-container>
        </v-card>
      </v-dialog>
      <picker-dialog :shared="sharedPicker" ref="picker" />
      <CellRender v-if="false" />
    </v-container>
  </v-app>
</template>

<script>
import moment from "moment";
import { AgGridVue } from "ag-grid-vue";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import "@/style/ag-theme-gs.css";
import { HeaderClass } from "@/components-v1/GrowthModel/RealFValueIput/HeaderClass";
import CellRender from "@/components-v1/GrowthModel/RealFValueIput/CellRender.vue";
import PickerDialog from "@/components-v1/common/Ph2ColorPicker.vue";
import { useGrowthFAll } from "@/api/TopStateGrowth/GEFValue/index";
import { useGrowthFDataUpdate } from "@/api/TopStateGrowth/GEActualValueInput/index";
import { DialogController } from "@/lib/mountController.js";

var headerClass = new HeaderClass();

export default {
  name: "FValue",
  props: {
    shared /** MountController */: { required: true },
  },
  components: {
    AgGridVue,
    CellRender,
    PickerDialog,
  },
  data() {
    return {
      date: moment().format("YYYY-MM-DD"),
      isDialog: false,
      param: null,
      params: [],
      columnDefs: null,
      columnDefsDefault: headerClass.getDefaultHeader(),
      columnDefsEdit: headerClass.getEditHeader(),
      //選択した圃場名指定
      fieldName: null,
      fieldId: null,
      //選択したデバイス名指定
      deviceName: null,
      deviceId: null,
      //選択した年度
      year: null,
      //現在の累積F値
      fValueInterval: null,
      rowData: null,
      isEditMode: false,
      gridOptions: null,
      gridApi: null,
      defaultColDef: null,
      sharedPicker: new DialogController(),
    };
  },
  beforeMount() {
    this.gridOptions = {};
    this.columnDefs = this.columnDefsDefault;
    this.defaultColDef = {
      editable: false,
    };
  },
  mounted() {
    this.shared.mount(this);
    this.gridApi = this.gridOptions.api;
    this.gridColumnApi = this.gridOptions.columnApi;
  },
  methods: {
    initialize: function () {
      this.isDialog = true;
      //圃場名
      this.fieldName = this.$store.getters.selectedField.name;
      this.fieldId = this.$store.getters.selectedField.id;
      //デバイス名
      this.deviceName = this.$store.getters.selectedDevice.name;
      this.deviceId = this.$store.getters.selectedDevice.id;
      //年度
      this.year = this.$store.getters.selectedYear.id;
      this.getUseGrowthFAll();
    },
    close: function () {
      this.isDialog = false;
    },
    //gridApi使用設定
    onGridReady: function () {},
    //F値入力欄追加 (削除)
    //累積値自動計算
    makeAccumulation: function (params) {
      //aggridデータ
      const rowDatas = this.rowData;
      let num = 0;
      rowDatas.map((data, i) => {
        num = num + Number(data.intervalF);
        this.rowData[i].accumulatedF = num;
      });
      num = 0;
      // this.gridApi.refreshCells({ force: true });
      params.api.refreshCells({
        columns: ["accumulatedF"],
        force: true,
      });
    },
    //ag-gridのデータ変更し実行（F間隔変更処理のみに改修必要）
    onColumnValueChanged: function (params) {
      //累積値変更自動設定
      this.makeAccumulation(params);
    },
    // ボタンクリックされた場合
    onButtonClicked(params) {
      if (params.column.colId == "color") {
        if (params.colDef.editable) {
          this.sharedPicker.setUp(
            this.$refs.picker,
            function (picker) {
              picker.initialize(params.data.color);
            },
            function (color) {
              this.rowData[params.rowIndex].color = color;
              params.api.refreshCells({ columns: ["color"], force: true });
            }.bind(this)
          );
        }
      }
      if (
        params.column.colId == "action" &&
        params.event.target.dataset.action
      ) {
        let action = params.event.target.dataset.action;
        if (action == "delete") {
          params.api.applyTransaction({
            remove: [params.node.data],
          });
          const rowDatas = this.rowData;
          rowDatas.map((item, i) => {
            if (item.order === params.node.data.order) {
              this.rowData.splice(i, 1);
              return;
            }
          });
        } else if (action == "add") {
          var copy = Object.assign({}, params.data);
          copy.id = null;
          this.rowData.splice(params.data.order, 0, copy);
          var i = 1;
          for (const item of this.rowData) {
            item.order = i++;
          }
          this.makeAccumulation(params);
        }
      }
    },
    //ag-gridのテーブル形式に変更
    changeGrowthForm: function (data) {
      let changeData = data;
      //stageStart順にASC
      changeData.sort(function (a, b) {
        if (a.stageStart > b.stageStart) {
          return 1;
        } else {
          return -1;
        }
      });
      let dataTypeArr = [];
      changeData.map((data, i) => {
        const elStageData = data.stageStart + "-" + data.stageEnd;
        let dataType = {
          ...data,
          order: i + 1,
          elStage: elStageData,
        };
        dataTypeArr.push(dataType);
      });
      return dataTypeArr;
    },
    //データ保存形式に変更
    saveGrowthForm: function (data) {
      let changeData = data;
      let dataTypeArr = [];
      changeData.map((data) => {
        let elStageData = data.elStage.split("-");
        let targetDateSet = "0000-00-00";
        if (data.targetDate) targetDateSet = data.targetDate;
        let dataType = {
          id: data.id,
          stageName: data.stageName,
          stageStart: parseInt(elStageData[0]),
          stageEnd: parseInt(elStageData[1]),
          intervalF: parseInt(data.intervalF),
          accumulatedF: data.accumulatedF,
          deviceId: parseInt(this.deviceId),
          color: data.color,
          createdAt: null,
          updatedAt: null,
          targetDate: targetDateSet,
          year: this.year,
        };
        dataTypeArr.push(dataType);
      });
      return dataTypeArr;
    },
    getUseGrowthFAll() {
      //生育推定F値データ取得
      useGrowthFAll(this.year, this.deviceId)
        .then((response) => {
          //成功時
          const results = response["data"];
          const GrowthRowData = this.changeGrowthForm(results.data);
          this.rowData = GrowthRowData;
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //変更データ保存API
    save: function () {
      const GrowthRowData = this.saveGrowthForm(this.rowData);
      //変更F値変更API
      const data = {
        deviceId: parseInt(this.deviceId),
        year: this.year,
        list: GrowthRowData,
      };
      //生育推定実績値更新
      useGrowthFDataUpdate(data)
        .then((response) => {
          //成功時
          const { status, message } = response["data"];
          if (status != 0) {
            throw new Error(message);
          }
          alert("更新を完了しました。");
        })
        .catch((error) => {
          //失敗時
          alert("更新に失敗しました。");
          console.log(error);
        });
    },
  },
  watch: {
    isEditMode() {
      if (this.isEditMode) {
        this.columnDefs = this.columnDefsEdit;
      } else {
        this.columnDefs = this.columnDefsDefault;
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.action-button {
  border: none;
  color: black;
  padding: 3px 12px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  opacity: 0.7;
  background-color: white;
}
</style>

