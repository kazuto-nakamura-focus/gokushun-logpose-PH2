<!--生育実績値入力画面 -->
<!--F値テーブル参照画面案 -->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="700" height="700">
        <v-card>
          <v-card-title>実績値入力</v-card-title>
          <v-container>
            <v-row no-gutters>
              <v-col :cols="3">
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
              <v-col>
                <v-card-text>
                  年度<br />
                  <p class="font-weight-bold">{{ year }}</p>
                </v-card-text>
              </v-col>
            </v-row>
            <v-card-text>
              現在の累積F値<br />
              <p class="font-weight-bold">{{ fValueInterval }}</p>
            </v-card-text>
            <div style="height: 250px">
              <div style="height: 250px; box-sizing: border-box">
                <AgGridVue ref="editDataSet" style="width: 100%; height: 100%" class="ag-theme-gs" sizeColumn
                  :columnDefs="columnDefs" :rowData="rowData" @grid-ready="onGridReady" :gridOptions="gridOptions"
                  :defaultColDef="defaultColDef" @cell-clicked="onCellClicked">
                </AgGridVue>
              </div>
            </div>
          </v-container>
          <div class="GS_ButtonArea">
            <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="save()">キャンセル</v-btn>
          </div>
        </v-card>
      </v-dialog>
    </v-container>
    <v-dialog v-model="isDialogEdit" width="700" height="700">
      <EditDialog ref="editDataSet" :selectedData="selectedData" :isDialogEdit="isDialogEdit"
        @achievementValueDataSave="achievementValueDataSave" @cancel="cancel" />
    </v-dialog>
  </v-app>
</template>
<script>
import moment from "moment";
import { AgGridVue } from "ag-grid-vue";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import "@/style/ag-theme-gs.css"
import EditDialog from "./EditDialog/index.vue"
import { useGrowthFAll } from "@/api/TopStateGrowth/GEFValue/index";
import { useGrowthFData } from "@/api/TopStateGrowth/GEActualValueInput/index"
import { useGrowthFDataUpdate } from "@/api/TopStateGrowth/GEActualValueInput/index"

export default {
  name: 'GEActualValueInput',
  props: {
    shared /** MountController */: { required: true },
  },
  components: {
    AgGridVue,
    EditDialog,
  },
  data() {
    return {
      date: moment().format("YYYY-MM-DD"),//new Date().toISOString().substring(0, 10),
      isDialog: false,
      param: null,
      params: [],
      columnDefs: [
        { field: "order", headerName: "No.", suppressSizeToFit: true, resizable: true, width: 80, },
        { field: "stageName", headerName: "生育期名", resizable: true, width: 115, },
        { field: "elStage", headerName: "E-L Stage間隔", resizable: true, width: 115, },
        { field: "intervalF", headerName: "F値間隔", resizable: true, width: 115, },
        { field: "accumulatedF", headerName: "累積F値", resizable: true, width: 115, },
        { field: "targetDate", headerName: "実績", resizable: true, valueFormatter: this.valueFormatter, width: 125, }, //cc
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

      gridOptions: null,
      gridApi: null,
      defaultColDef: null,
      pickerStatus: false,
      picker: null,
      // dataChangeStatus: null,
      isDialogEdit: false,
      selectedData: null,
      originSelectedDataFValue: null,
    };
  },
  beforeMount() {
    this.gridOptions = {};
    this.defaultColDef = {
      editable: false
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
      this.getUseGrowthFAll()
      this.getUseGrowthFData(this.date, this.deviceId)
    },
    close: function () {
      //データ初期化
      // this.rowData = [...this.rowDataOrigin]
      this.gridApi.refreshCells({ force: true });
      this.isDialog = false;
      this.shared.onConclude();
    },
    save: function () {
      this.isDialog = false;
    },
    onGridReady: function () {
    },
    //実績値入力画面展開
    onCellClicked(params) {
      // * クリックされたフィールドの情報を取得する
      this.gridApi = params.api;
      this.gridColumnApi = params.columnApi;

      if (params.column.colId === 'targetDate') {
        // 未定義の場合はNULLに設定
        if(params.node.data.targetDate === undefined){
          params.node.data.targetDate = null;
        }
        const editOrder = params.node.data.order
        // 対象となる
        const editTargetDate = params.node.data.targetDate

        //初期今日の日付データ設定（初期実績値がnullの場合：今日日付をdefault値に保存）
        if (!editTargetDate) {
          const today = this.date
          this.rowData.map((item, i) => {
            if (item.order === editOrder) {
              this.rowData[i].targetDate = today
              //gridApi refresh
              // this.gridApi.refreshCells({
              //   force: true,
              // });
              return
            }
          });
        }

        //選択データをag-grid形に変更
        let agGridRowDataFomat = []
        agGridRowDataFomat.push(params.node.data)
        this.selectedData = agGridRowDataFomat
        //入力ダイアログ展開
        this.isDialogEdit = !this.isDialogEdit

        this.pickerStatus = true
        if (!params.value) {
          //実績値未入力状態
          // this.dataChangeStatus = editOrder
          this.pickerStatus = true
        }
      }
    },
    getUseGrowthFAll() {
      //生育推定F値データ取得
      useGrowthFAll(this.year, this.deviceId)
        .then((response) => {
          //成功時
          const results = response["data"];
          this.originGrowthFAllData = results.data
          const GrowthRowData = this.changeGrowthForm(results.data)
          this.rowData = GrowthRowData
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    getUseGrowthFData(year, deviceId) {
      //生育推定実績値取得
      useGrowthFData(year, deviceId)
        .then((response) => {
          //成功時
          const results = response["data"];
          results.data
            ? this.fValueInterval = results.data.value
            : this.fValueInterval = 0
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //累積値自動計算
    makeAccumulation: function (sendData) {
      //aggridデータ
      const rowDatas = sendData
      let num = 0;
      rowDatas.map((data, i) => {
        num = num + Number(data.intervalF)
        this.rowData[i].accumulatedF = num
      })
      num = 0
      this.gridApi.refreshCells({ force: true });
      const GrowthRowData = this.saveGrowthForm(rowDatas)
      //変更F値変更API
      const data = {
        deviceId: this.deviceId,
        year: this.year,
        list: GrowthRowData,
      }
      //生育推定実績値更新
      useGrowthFDataUpdate(data)
        .then((response) => {
          //成功時
          const results = response["data"];
          console.log(results);
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
    //変更データ保存
    achievementValueDataSave(data) {
      this.rowData.map((item, i) => {
        if (item.order === data.order) {
          this.rowData[i].intervalF = data.intervalF
          this.rowData[i].accumulatedF = data.accumulatedF
          this.rowData[i].targetDate = data.targetDate
          return
        }
      });
      const sendData = this.rowData
      this.gridApi.refreshCells({ force: true });
      //F値間隔再設定
      this.makeAccumulation(sendData)
      this.isDialogEdit = !this.isDialogEdit
    },
    //実績値入力画面キャンセル
    cancel() {
      //データ初期化()
      this.getUseGrowthFAll()
      this.isDialogEdit = !this.isDialogEdit
    },
    valueFormatter(params) {
      if (params.value) {
        return params.value;
      } else {
        return '実績値入力';
      }
    },
    //ag-gridのテーブル形式に変更
    changeGrowthForm: function (data) {
      let changeData = data
      //stageStart順にASC
      changeData.sort(function (a, b) {
        if (a.stageStart > b.stageStart) {
          return 1;
        } else {
          return -1;
        }
      })
      let dataTypeArr = []
      changeData.map((data, i) => {
        const elStageData = data.stageStart + "-" + data.stageEnd
        let dataType = {
          "order": i + 1,
          "stageName": data.stageName,
          "elStage": elStageData,
          "intervalF": data.intervalF,
          "accumulatedF": data.accumulatedF,
          "targetDate": data.targetDate,
        }
        dataTypeArr.push(dataType)
      })
      return dataTypeArr
    },
    //データ保存形式に変更
    saveGrowthForm: function (data) {
      let changeData = data
      let dataTypeArr = []
      changeData.map((data) => {

        let elStageData = data.elStage.split('-');
        let targetDateSet = 0

        if (data.targetDate)
          targetDateSet = data.targetDate

        let dataType = {
          "stageName": data.stageName,
          "stageStart": elStageData[0],
          "stageEnd": elStageData[1],
          "intervalF": data.intervalF,
          "accumulatedF": data.accumulatedF,
          "deviceId": parseInt(this.deviceId),
          "createdAt": null,
          "updatedAt": null,
          "targetDate": targetDateSet,
          "year": parseInt(this.year),
        }
        dataTypeArr.push(dataType)
      })
      return dataTypeArr
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
