<!--実績値入力画面-->
<template>
  <v-card>
    <v-card-title>実績値入力</v-card-title>

    <v-container>
      <!-- タイトル部分 -->
      <input-header ref="titleHeader" />

      <v-card-text>
        最後{{ inputArg.date }}の累積F値
        <br />
        <p class="font-weight-bold">{{ inputArg.accumulatedFvalue }}</p>
      </v-card-text>

      <div style="height: 250px">
        <div style="height: 250px; box-sizing: border-box">
          <AgGridVue
            style="width: 100%; height: 100%"
            class="ag-theme-gs"
            sizeColumn
            :columnDefs="columnDefs"
            :rowData="rowData"
            :gridOptions="gridOptions"
            :defaultColDef="defaultColDef"
            @cell-clicked="onCellClicked"
          ></AgGridVue>
        </div>
      </div>

      <!-- 実績値の日付入力画面 -->
      <v-dialog v-model="pickerStatus" width="400" height="400" permanent>
        <v-date-picker v-model="picker" @input="achievementValueDataGet"></v-date-picker>
      </v-dialog>
    </v-container>

    <div class="GS_ButtonArea">
      <v-btn
        color="primary"
        class="ma-2 white--text"
        elevation="2"
        @click="achievementValueDataSave()"
      >保存</v-btn>

      <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="cancel()">キャンセル</v-btn>
    </div>
  </v-card>
</template>

<script>
import moment from "moment";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import { AgGridVue } from "ag-grid-vue";
import "@/style/ag-theme-gs.css";
import {
  useGrowthFData,
  useGrowthFDataByDate,
} from "@/api/TopStateGrowth/GEActualValueInput/index";
import InputHeader from "@/components-v1/parts/変数入力画面ヘッダー.vue";

export default {
  name: "EditDialog",
  props: {
    selectedData: Array,
    isDialogEdit: Boolean,
    selectedItems: Object,
    inputArg: Object,
  },
  components: {
    AgGridVue,
    InputHeader,
  },
  data() {
    return {
      columnDefs: null,
      defaultColDef: null,
      param: null,
      params: [],
      columnTypes: null,
      gridOptions: {},
      gridApi: null,

      pickerStatus: false,
      picker: null,
      dataChangeStatus: null,

      fieldName: this.$store.getters.selectedField.name,
      deviceId: this.$store.getters.selectedDevice.id,
      year: this.$store.getters.selectedYear.id,
      date: moment().format("YYYY-MM-DD"),
      accumulatedF: 0,

      fValueInterval: 0,
      elStageIntervalFormatterStatus: false,
      originSelectData: [...this.selectedData],
      rowData: [],
      setOriginSelectedDataFValue: this.originSelectedDataFValue,
      achievementValueDataSaveData: [],
      saveIntervalF: 0,
      saveAccumulatedF: 0,
    };
  },
  beforeMount() {
    //ag-grid設定
    //F値変更カレンダー
    const elStageIntervalFormatter = (params) => {
      if (this.elStageIntervalFormatterStatus) {
        this.fValueInterval = Math.round(this.fValueInterval * 100) / 100;
        return `${params.value} -> ${this.fValueInterval}`;
      } else {
        return params.value;
      }
    };
    //F値選択カレンダ
    const valueFormatter = (params) => {
      if (params.value || params.value == 0) {
        return params.value;
      } else {
        return "実績値入力";
      }
    };
    //累積F値選択カレンダ
    const elStageAccumulatedFormatter = (params) => {
      if (this.elStageIntervalFormatterStatus) {
        return (
          this.saveAccumulatedF - (this.saveIntervalF - this.fValueInterval)
        );
      } else {
        return params.value;
      }
    };
    this.columnDefs = [
      {
        field: "id",
        suppressSizeToFit: true,
        width: 80,
        hide: true,
      },
      {
        field: "order",
        headerName: "No.",
        suppressSizeToFit: true,
        resizable: true,
        width: 40,
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
        width: 115,
      },
      {
        field: "intervalF",
        headerName: "F値間隔",
        resizable: true,
        valueFormatter: elStageIntervalFormatter,
        width: 120,
      },
      {
        field: "accumulatedF",
        headerName: "累積F値",
        resizable: true,
        valueFormatter: elStageAccumulatedFormatter,
        width: 120,
      },
      {
        field: "actualDate",
        headerName: "実績",
        resizable: true,
        valueFormatter: valueFormatter,
        cellRenderer: (params) => {
          if (params.value) {
            // return params.value + '<v-icon small class="mr-2">mdi-pencil</v-icon>';
            // return '<v-icon small class="mr-2">mdi-pencil</v-icon>';
            return (
              params.value +
              '<i data-v-539683ac="" aria-hidden="true" class="v-icon notranslate mdi mdi-pencil theme--light" style="font-size: 36px;"></i>'
            );
          } else {
            return '<button class="v-btn v-btn--has-bg theme--light elevation-3 v-size--small primary">実績値入力</button>';
          }
        },
        width: 160,
      }, //cc
    ];
    this.defaultColDef = {
      editable: false,
    };
  },
  mounted() {
    this.gridApi = this.gridOptions.api;
    this.gridColumnApi = this.gridOptions.columnApi;
    //展開
    //現在の累積F値設定
    this.init();
  },
  updated() {},
  methods: {
    init: function () {
      this.$nextTick(
        function () {
          this.$refs.titleHeader.initialize(this.selectedItems);
        }.bind(this)
      );
      let setData = this.selectedData[0];
      this.rowData = this.selectedData;
      this.saveIntervalF = setData.intervalF;
      this.saveAccumulatedF = setData.accumulatedF;
      // Pickerに日付を設定する
      this.setPickerDate(setData.actualDate);
    },
    //* ============================================
    // Pickerに日付を設定する
    //* ============================================
    setPickerDate: function (actualDate) {
      // 指定が無い場合
      if (null == actualDate) {
        this.picker = moment().format("YYYY-MM-DD");
        // 推定値
      } else if (actualDate.charAt(0) == "(") {
        let dateTmp = actualDate.substring(1, 11);
        this.picker = dateTmp;
        // 実測値
      } else {
        this.picker = actualDate;
      }
    },
    //* ============================================
    // 編集ボタンが押された場合
    //* ============================================
    onCellClicked(params) {
      this.gridApi = params.api;
      this.gridColumnApi = params.columnApi;
      if (params.column.colId == "actualDate") {
        this.pickerStatus = true;
        this.dataChangeStatus = params.node.data.order;
        // this.achievementValueDataSaveData.order = this.dataChangeStatus
      }
    },
    //* ============================================
    // カレンダーで日付が選択された時
    //* ============================================
    async achievementValueDataGet() {
      if (this.picker) {
        // チェックAPIをコール
        await useGrowthFDataByDate(this.rowData[0].id, this.picker)
          .then((response) => {
            const { status, message, data } = response["data"];
            if (status != 0) {
              alert(message);
              throw Error(message);
            }
            this.rowData[0].actualDate = this.picker;
            this.rowData[0].accumulatedF =
              Math.floor(data.accumulated * 100) / 100;
            this.rowData[0].intervalF = Math.floor(data.interval * 100) / 100;
          })
          .catch((error) => {
            //失敗時。
            alert("指定日のデータの確認に失敗しました。");
            console.log(error);
          });

        this.gridApi.refreshCells({ force: true });
        //view F値、日付変更
        // this.changeAchievement(this.getUseGrowthFData);
        // this.elStageIntervalFormatterStatus = true;
      } else {
        alert("日付を選択してください。");
      }
      this.pickerStatus = false;
    },
    //実績値反映
    /*async changeAchievement(callback) {
      //選択日の実績値取得
      await callback(this.picker, this.deviceId);
      this.rowData[0].actualDate = this.picker;
      this.rowData[0].accumulatedF = this.accumulatedF;
      await this.gridApi.refreshCells({ force: true });
      this.rowData[0].intervalF = this.fValueInterval;
      this.achievementValueDataSaveData = this.rowData[0];
    },*/
    //実績値保存
    achievementValueDataSave() {
      this.elStageIntervalFormatterStatus = false;
      this.pickerStatus = false;
      //DB保存API作成
      this.$emit("achievementValueDataSave", this.achievementValueDataSaveData);
    },
    cancel() {
      this.elStageIntervalFormatterStatus = false;
      this.rowData = [];
      this.picker = null;
      this.fValueInterval = 0;
      this.$emit("cancel");
    },
    async getUseGrowthFData(date) {
      //生育推定実績値取得
      await useGrowthFData(date, this.deviceId)
        .then((response) => {
          const results = response["data"];
          let value = Math.round(results.data.value * 100) / 100;
          // * 累積F値
          this.accumulatedF = value;
          // * インターバル
          this.fValueInterval = value - this.inputArg.prevFValue;
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
  },
  watch: {
    isDialogEdit: function () {
      if (this.isDialogEdit) {
        this.init();
      }
    },
  },
};
</script>