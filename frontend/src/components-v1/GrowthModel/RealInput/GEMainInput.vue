<!--生育実績値入力画面 -->
<!--F値テーブル参照画面案 -->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="800" height="700" overflow="hidden">
        <v-card v-card style="overflow: hidden">
          <v-card-title>実績値入力</v-card-title>

          <!-- タイトル部分 -->

          <input-header ref="titleHeader" />

          <!-- 入力部分 -->

          <v-container>
            <v-card-text>
              最後{{ inputArg.date }}の累積F値
              <br />

              <p class="font-weight-bold">{{ inputArg.accumulatedFvalue }}</p>
            </v-card-text>

            <div style="height: 250px">
              <div style="height: 250px; box-sizing: border-box">
                <AgGridVue
                  ref="editDataSet"
                  style="width: 100%; height: 100%; font-size: 9pt"
                  class="ag-theme-gs"
                  sizeColumn
                  :columnDefs="columnDefs"
                  :rowData="rowData"
                  @grid-ready="onGridReady"
                  :gridOptions="gridOptions"
                  :defaultColDef="defaultColDef"
                  @cell-clicked="onCellClicked"
                ></AgGridVue>
              </div>
            </div>
          </v-container>

          <div class="GS_ButtonArea">
            <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="close()">閉じる</v-btn>
          </div>
        </v-card>
      </v-dialog>

      <v-dialog v-model="isDialogEdit" width="700" height="700">
        <EditDialog
          ref="geInput"
          :selectedData="selectedData"
          :isDialogEdit="isDialogEdit"
          :selectedItems="selectedItems"
          :inputArg="inputArg"
          @achievementValueDataSave="achievementValueDataSave"
          @cancel="cancel"
        />
      </v-dialog>
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
import EditDialog from "./GEInput.vue";
import CellRender from "./CellRender.vue";
import { useGrowthFAll } from "@/api/TopStateGrowth/GEFValue/index";
import { useGrowthFData } from "@/api/TopStateGrowth/GEActualValueInput/index";
import { useGrowthFDataUpdate } from "@/api/TopStateGrowth/GEActualValueInput/index";
import InputHeader from "@/components-v1/parts/変数入力画面ヘッダー.vue";

export default {
  name: "GEActualValueInput",
  props: {
    shared /** MountController */: { required: true },
  },
  components: {
    AgGridVue,
    EditDialog,
    InputHeader,
    CellRender,
  },
  data() {
    return {
      date: moment().format("YYYY-MM-DD"), //new Date().toISOString().substring(0, 10),
      isDialog: false,
      param: null,
      params: [],
      columnDefs: [
        {
          field: "order",
          headerName: "No.",
          suppressSizeToFit: true,
          resizable: true,
          width: 80,
        },
        {
          field: "stageName",
          headerName: "生育期名",
          resizable: true,
          width: 115,
        },
        {
          field: "elStage",
          headerName: "E-L Stage間隔",
          resizable: true,
          width: 120,
        },
        {
          field: "intervalF",
          headerName: "F値間隔",
          resizable: true,
          width: 90,
        },
        {
          field: "accumulatedF",
          headerName: "累積F値",
          resizable: true,
          width: 90,
        },
        {
          field: "actualDate",
          headerName: "実績(推定)日",
          resizable: true,
          width: 120,
        },
        {
          field: "action",
          headerName: "",
          cellRenderer: "CellRender",
          colId: "action",
          editable: false,
          resizable: false,
          width: 140,
        },
      ],
      //選択した圃場名指定
      fieldName: null,
      fieldId: null,
      //選択したデバイス名指定
      deviceName: null,
      deviceId: null,
      //選択した年度
      year: null,
      //現在の累積F値
      fValueInterval: 0,
      rowData: null,
      originGrowthFAllData: null,
      selectedItems: {},
      // 入力画面の引数
      inputArg: {
        prevFValue: 0,
        accumulatedFvalue: 0,
        date: null,
      },
      gridOptions: null,
      gridApi: null,
      defaultColDef: null,
      pickerStatus: false,
      picker: null,
      // dataChangeStatus: null,
      isDialogEdit: false,
      selectedData: null,
      originSelectedDataFValue: null,
      // 値が更新されたかどうかのフラグ
      isUpdated: false,
    };
  },
  beforeMount() {
    this.gridOptions = {};
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
    initialize: function (data) {
      this.$nextTick(
        function () {
          this.$refs.titleHeader.initialize(data.menu);
        }.bind(this)
      );
      this.selectedItems = data.menu;
      this.isDialog = true;
      // 値が更新されたかどうかのフラグ
      this.isUpdated = false;
      //圃場名
      this.fieldName = this.selectedItems.selectedField.name;
      this.fieldId = this.selectedItems.selectedField.id;
      //デバイス名
      this.deviceName = this.selectedItems.selectedDevice.name;
      this.deviceId = this.selectedItems.selectedDevice.id;
      //年度
      this.year = this.selectedItems.selectedYear.id;
      this.getUseGrowthFAll();
    },
    close: function () {
      //データ初期化
      // this.rowData = [...this.rowDataOrigin]
      if (this.gridApi) this.gridApi.refreshCells({ force: true });
      this.isDialog = false;
      this.shared.onConclude(this.isUpdated);
    },
    save: function () {
      this.isDialog = false;
    },
    onGridReady: function () {},
    //実績値入力画面展開
    onCellClicked(params) {
      // * クリックされたフィールドの情報を取得する
      this.gridApi = params.api;
      this.gridColumnApi = params.columnApi;
      // * 実績値入力ボタンを押下した場合
      if (params.column.colId === "action") {
        // 未定義の場合はNULLに設定
        if (params.node.data.actualDate === undefined) {
          params.node.data.actualDate = null;
        }
        const editOrder = params.node.data.order;
        // 対象となる
        const editTargetDate = params.node.data.actualDate;

        //初期今日の日付データ設定（初期実績値がnullの場合：今日日付をdefault値に保存）
        if (!editTargetDate) {
          const today = this.date;
          this.rowData.map((item, i) => {
            if (item.order === editOrder) {
              this.rowData[i].actualDate = today;
              //gridApi refresh
              // this.gridApi.refreshCells({
              //   force: true,
              // });
              return;
            }
          });
        }

        //選択データをag-grid形に変更
        let agGridRowDataFomat = [];
        agGridRowDataFomat.push(params.node.data);
        this.selectedData = agGridRowDataFomat;
        let value =
          params.node.rowIndex > 0
            ? this.rowData[params.node.rowIndex - 1].accumulatedF
            : 0;
        this.inputArg.prevFValue = value;
        //入力ダイアログ展開
        this.isDialogEdit = !this.isDialogEdit;
        this.pickerStatus = true;
        if (!params.value) {
          //実績値未入力状態
          // this.dataChangeStatus = editOrder
          this.pickerStatus = true;
        }
      }
    },
    //* ============================================
    // 生育推定F値データ取得
    //* ============================================
    getUseGrowthFAll() {
      useGrowthFAll(this.year, this.deviceId)
        .then((response) => {
          //成功時
          const results = response["data"];
          moment.locale("ja");
          this.originGrowthFAllData = results.data;
          const GrowthRowData = this.changeGrowthForm(results.data);

          this.rowData = GrowthRowData;

          this.getUseGrowthFData(this.date, this.deviceId);
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //* ============================================
    // 最新実績値取得
    //* ============================================
    getUseGrowthFData(year, deviceId) {
      // APIコール
      useGrowthFData(year, deviceId)
        .then((response) => {
          //成功時
          const { status, message, data } = response["data"];
          if (status != 0) {
            this.inputArg.date = " - ";
            this.inputArg.accumulatedFvalue = " - ";
            throw new Error(message);
          }
          this.inputArg.date = "(" + data.date.substring(0, 10) + ")";
          this.inputArg.accumulatedFvalue = data.value;
        })
        .catch((error) => {
          //失敗時
          console.log(error);
          alert("実績値取得に失敗しました。");
        });
    },
    //累積値自動計算
    makeAccumulation: function (sendData) {
      //aggridデータ
      const rowDatas = sendData;
      let num = 0;

      for (var i = 0; i < rowDatas.length; i++) {
        const item = rowDatas[i];
        num = num + Number(item.intervalF);
        item.accumulatedF = num;
      }

      const GrowthRowData = this.saveGrowthForm(rowDatas);
      //変更F値変更API
      const data = {
        deviceId: this.deviceId,
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
          alert("実績値の更新を完了しました。");
          this.gridApi.refreshCells({ force: true });
          this.isDialogEdit = false;
          this.isUpdated = true;
        })
        .catch((error) => {
          //失敗時
          console.log(error);
          alert("実績値の更新に失敗しました。");
        });
    },
    //変更データ保存
    achievementValueDataSave(data) {
      const rowData = this.rowData;
      for (var i = 0; i < rowData.length; i++) {
        const item = rowData[i];
        if (item.order === data.order) {
          item.intervalF = data.intervalF;
          item.accumulatedF = data.accumulatedF;
          item.actualDate = data.actualDate;
          break;
        }
      }

      //F値間隔再設定
      this.makeAccumulation(this.rowData);
      // データが更新されたかどうかのフラグ
      this.isUpdated = true;
    },
    //実績値入力画面キャンセル
    cancel() {
      //データ初期化()
      this.getUseGrowthFAll();
      this.isDialogEdit = false;
    },
    valueFormatter(params) {
      if (params.value) {
        return params.value;
      } else {
        return "実績値入力";
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
        if (null != data.actualDate) {
          let tmp = data.actualDate.substring(0, 10) + " 00:00:00";
          let day = moment(tmp).add(1, "d");

          data.actualDate = day.format("YYYY-MM-DD");
        } else if (null != data.estimateDate) {
          let tmp = data.estimateDate.substring(0, 10) + " 00:00:00";
          let day = moment(tmp).add(1, "d");
          data.actualDate = "(" + day.format("YYYY-MM-DD") + ")";
        }

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
    saveGrowthForm: function (baseData) {
      let changeData = baseData;
      let dataTypeArr = [];

      changeData.map((data) => {
        let elStageData = data.elStage.split("-");

        let dataType = {
          id: data.id,
          stageName: data.stageName,
          stageStart: elStageData[0],
          stageEnd: elStageData[1],
          intervalF: data.intervalF,
          accumulatedF: data.accumulatedF,
          deviceId: parseInt(this.deviceId),
          createdAt: null,
          updatedAt: null,
          actualDate: null,
          estimateDate: null,
          color: data.color,
          year: parseInt(this.year),
        };
        // ターゲット日の設定
        if (data.actualDate) {
          if (data.actualDate.charAt(0) == "(") {
            dataType.estimateDate = data.actualDate.substring(1, 11);
          } else {
            dataType.actualDate = data.actualDate;
          }
        }

        dataTypeArr.push(dataType);
      });
      return dataTypeArr;
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
</style>./GEInput.vue
