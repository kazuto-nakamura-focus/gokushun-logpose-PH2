<!--葉面積実績値入力-->
<template>
  <v-container>
    <div style="margin-top:0;">葉面積・葉枚数</div>
    <v-subheader class="ma-0 mt-n3 pa-0">(測定タイミング : 萌芽後任意のタイミングで実施してください)</v-subheader>
    <div ref="guideComment" class="comment">
      <p>入力後リターンキーを押してください。</p>
    </div>
    <div style="height: 420px; box-sizing: border-box">
      <p v-if="!isAllValueInputted" class="error">{{this.messages.NotNumberAll}}</p>
      <AgGridVue
        ref="agGrid"
        style="width: 100%; height: 100%"
        class="ag-theme-gs"
        :columnDefs="columnDefs"
        @grid-ready="onGridReady"
        :rowData="rowData"
        sizeColumn
        @cell-clicked="onCellClicked"
        @cell-value-changed="onColumnValueChanged"
        @cellMouseOver="onCellMouseOver"
        @cellFocused="onCellFocused"
        @cellKeyPress="onCellKeyPress"
      ></AgGridVue>
    </div>
    <div class="GS_ButtonArea">
      <v-btn
        color="primary"
        class="ma-2 white--text"
        elevation="2"
        :disabled="(buttonStatus!=0)||(!isAllValueInputted)"
        @click="register()"
      >保存</v-btn>
      <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="close()">閉じる</v-btn>
    </div>
    <div class="datePicker" ref="dateInput">
      <v-date-picker
        v-model="dateInfo.date"
        :min="dateInfo.minDate"
        :max="dateInfo.maxDate"
        @change="handleChangeDate"
        locale="jp-ja"
      ></v-date-picker>
    </div>
  </v-container>
</template>
<script>
import { AgGridVue } from "ag-grid-vue";
import messages from "@/assets/messages.json";

import {
  useLeafValueAreaAndCount,
  useLeafValueAllAreaAndCountDetail,
} from "@/api/TopStateGrowth/LAActualValueInput";
//* ============================================
// セルのステータスを設定
//* ============================================
function setStatus(params, number, bool) {
  let row = params.node.parent.allLeafChildren[params.rowIndex];
  if (row.status === undefined) {
    row.status = new Number(0);
  }
  if (bool) row.status = (row.status | number) - number;
  else row.status = row.status | number;
}
//* ============================================
// 行削除を表示
//* ============================================
function RemoveCellRenderer() {
  let eGui = document.createElement("div");
  eGui.innerHTML = `
  <div>
  <button data-action="remove" >-</button>
  </div>`;
  return eGui;
}
//* ============================================
// 行追加を表示
//* ============================================
function AddCellRenderer() {
  let eGui = document.createElement("div");
  eGui.innerHTML = `
  <div>
  <button data-action="add" >+</button>
  </div>`;
  return eGui;
}
//* ============================================
// セルの背景色を設定
//* ============================================
var regexp = new RegExp(/^([1-9]\d*|0)(\.\d+)?$/);
function setBackground(params, number, value) {
  //
  if (value == null || value.length == 0) {
    setStatus(params, number, false);
    return { border: "2px solid #f33" };
  } else if (!regexp.test(value)) {
    setStatus(params, number, false);
    return { border: "2px solid #f33" };
  } else {
    setStatus(params, number, true);
    return { border: "1px solid #fff" };
  }
}
export default {
  props: {
    /*
    target {deviceId, year}
    */
    target: Object,
  },

  data() {
    return {
      messages: messages,
      errormessage: "",
      isAllValueInputted: true,
      buttonStatus: 0,
      dateInfo: {
        date: null,
        minDate: null,
        maxDate: null,
        params: null,
      },
      columnDefs: [
        {
          field: "date",
          colId: "date",
          singleClickEdit: true,
          headerName: "日付",
          editable: false,
          resizable: true,
          width: 120,
          cellStyle: (params) => {
            return setBackground(params, 1, params.data.f);
          },
        },
        {
          field: "count",
          colId: "count",
          singleClickEdit: true,
          headerName: "新梢あたり葉枚数",
          editable: true,
          resizable: true,
          width: 140,
          cellStyle: (params) => {
            return setBackground(params, 2, params.data.f);
          },
        },
        {
          field: "averageArea",
          colId: "averageArea",
          singleClickEdit: true,
          headerName: "平均個葉面積(c㎡)",
          editable: true,
          resizable: true,
          width: 150,
          cellStyle: (params) => {
            return setBackground(params, 4, params.data.g);
          },
        },
        {
          field: "totalArea",
          colId: "totalArea",
          headerName: "実測樹幹葉面積(㎡)",
          editable: false,
          resizable: true,
          width: 150,
          valueGetter: (params) => {
            let value = params.data.count * params.data.averageArea;
            value = value / 10000;
            return Math.round(value * 10000) / 10000;
          },
          cellStyle: () => {
            return { backgroundColor: "#aaa" };
          },
        },
        {
          field: "estimatedArea",
          colId: "estimatedArea",
          headerName: "モデル推定値(㎡)",
          editable: false,
          resizable: true,
          width: 140,
          valueGetter: (params) => {
            let value = params.data.count * params.data.averageArea;
            value = value / 10000;
            return Math.round(value * 10000) / 10000;
          },
          cellStyle: () => {
            return { backgroundColor: "#aaa" };
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
      mouseX: 0,
      mouseY: 0,

      deviceId: this.target.deviceId,
      year: this.target.year,
      // 更新されたかどうかのフラグ
      isUpdated: false,

      skelton: {
        date: this.date,
        //* 新梢辺り葉枚数
        count: 0,
        //* 平均個葉面積
        averageArea: 0,
        //* 葉面積
        totalArea: 0,
        //* 推定値
        estimatedArea: 0,
      },
    };
  },

  components: {
    AgGridVue,
  },

  mounted() {
    this.callAPILeafValueAllAreaAndCountDetail();
  },
  methods: {
    // ======================================================
    // 新梢辺り葉枚数・平均個葉面積取得を行うAPIと処理
    // ======================================================
    callAPILeafValueAllAreaAndCountDetail: function () {
      console.log("afv");
      // * 実績値の取得
      useLeafValueAllAreaAndCountDetail(this.deviceId, this.year)
        .then((response) => {
          const { status, message, data } = response["data"];
          if (status == 0) {
            //* DatePickerの設定
            this.dateInfo.minDate = data.startDate;
            this.dateInfo.maxDate = data.endDate;
            //* ag-grid-vueの設定
            if (data.values.length > 0) {
              this.rowData = data.values;
              /*
	          private String date; //* 実施日
	          private Integer count; //* 新梢辺り葉枚数
	          private Float averageArea; //* 平均個葉面積
	          private Double totalArea; //* 葉面積
	          private Double estimatedArea; //* モデル値
            */
            } else {
              var row = Object.assign({}, this.skelton);
              this.rowData = [row];
            }
          } else {
            alert("新梢辺り葉枚数・平均個葉面積取得に失敗しました。");
            throw new Error(message);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    //* ============================================
    // 入力チェックとボタン表示の設定
    //* ============================================
    setStatus(status, bool) {
      if (!bool) {
        this.buttonStatus = this.buttonStatus | status;
      } else {
        this.buttonStatus = (this.buttonStatus | status) - status;
      }
      return bool;
    },
    // ======================================================
    // 葉面積データの登録を行う
    // ======================================================
    callAPIuseLeafValueAreaAndCount() {
      const data = {
        deviceId: this.device.id,
        year: this.year.id,
        values: this.rowData,
      };
      //新梢辺り葉枚数・平均個葉面積登録処理
      useLeafValueAreaAndCount(data)
        .then((response) => {
          const { status, message } = response["data"];
          if (status === 0) {
            alert("登録が完了しました。");
            this.isUpdated = true;
          } else {
            alert("登録が失敗しました。");
            throw new Error(message);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },

    //* ============================================
    // グリッドが表示された時
    //* ============================================
    //gridApi使用設定
    onGridReady: function (params) {
      this.gridApi = params.api;
      this.gridColumnApi = params.columnAPI;
      if (this.rowData == null || this.rowData.length == 0) {
        var row = Object.assign({}, this.skelton);
        this.rowData = [row];
      }
    },
    //* ============================================
    // セルの値が変化した場合
    //* ============================================
    onColumnValueChanged: function (param) {
      let items = param.node.parent.allLeafChildren;
      this.isAllValueInputted = true;
      for (const item of items) {
        if (0 !== item.status) {
          this.isAllValueInputted = false;
          break;
        }
      }
    },
    //* ============================================
    // 日付設定処理
    //* ============================================
    handleChangeDate() {
      this.hideDatePicker();
      let params = this.dateInfo.params;
      this.rowData[params.rowIndex].date = this.dateInfo.date;
      let rowIndex = params.rowIndex;
      this.$set(this.rowData, rowIndex, {
        ...this.rowData[rowIndex],
        date: this.dateInfo.date,
      });
    },
    //* ============================================
    // row追加・削除
    //* ============================================
    onCellClicked(params) {
      const gridApi = params?.api;
      const nodes = [];
      gridApi?.forEachNode((node) => nodes.push(node));
      if (params.column.colId === "remove") {
        this.rowData.splice(params.rowIndex, 1);
        params.api.applyTransaction({
          remove: [params.data],
        });

        if (nodes.length == 1) {
          let row = Object.assign({}, this.skelton);
          this.rowData.push(row);
          params.api.applyTransaction({
            add: [row],
          });
        }
      } else if (params.column.colId === "add") {
        let row = Object.assign({}, this.skelton);
        this.rowData.splice(1, 0, row);
        params.api.applyTransaction({
          add: [row],
        });
      }
    },
    //* ============================================
    // リターンキーが押されたとき、コメントを隠す
    //* ============================================
    onCellKeyPress(params) {
      // リターンキーが押されたときの処理を記述
      if (params.event.keyCode === 13) {
        this.hideComment();
      }
    },
    //* ============================================
    // マウスの座標軸を設定する
    //* ============================================
    onCellMouseOver(params) {
      this.mouseX = params.event.clientX;
      this.mouseY = params.event.clientY;
    },
    //* ============================================
    //  セルにフォーカスが移ったときコメントを表示
    //* ============================================
    onCellFocused(params) {
      if (
        params.column.colId == "count" ||
        params.column.colId == "gaverageArea"
      ) {
        this.showComment();
      } else {
        this.hideComment();
      }
      // * 日付セルにフォーカスが移った場合
      if (params.column.colId == "date") {
        this.dateInfo.params = params;
        this.dateInfo.date = this.rowData[params.rowIndex].date;
        this.showDatePicker();
      }
    },
    //* ============================================
    // マウスの座標軸上にコメントを表示
    //* ============================================
    showComment() {
      //* コメント位置を設定
      let y = this.mouseY + 10;
      let x = this.mouseX - 10;
      this.$refs.guideComment.style.top = "" + y + "px";
      this.$refs.guideComment.style.left = "" + x + "px";
      //* コメントを表示
      this.$refs.guideComment.style.display = "inline-block";
      // * コメント表示時は入力不可
      this.setStatus(32, false);
    },
    //* ============================================
    // マウスの座標軸上に日付入力を表示
    //* ============================================
    showDatePicker() {
      //* コメント位置を設定
      let y = this.mouseY + 10;
      let x = this.mouseX - 10;
      this.$refs.dateInput.style.top = "" + y + "px";
      this.$refs.dateInput.style.left = "" + x + "px";
      //* 日付入力を表示
      this.$refs.dateInput.style.display = "inline-block";
      // * 日付入力表示時は入力不可
      this.setStatus(64, false);
    },
    //* ============================================
    // コメントを隠す
    //* ============================================
    hideComment() {
      this.$refs.guideComment.style.display = "none";
      this.setStatus(32, true);
    },
    //* ============================================
    // 日付を隠す
    //* ============================================
    hideDatePicker() {
      this.$refs.dateInput.style.display = "none";
      this.setStatus(64, true);
    },
    //* ============================================
    // 閉じるアクション
    //* ============================================
    close() {
      this.$emit("close");
    },
  },
};
</script>
<style>
.datePicker {
  position: fixed;
  display: none;
  z-index: 200;
  border: 1px ridge #ff66ff;
}
.comment {
  position: fixed;
  display: none;
  margin: 1.5em 0;
  padding: 7px 10px;
  min-width: 120px;
  max-width: 100%;
  color: #555;
  font-size: 8pt;
  background: #fff;
  z-index: 100;
  border: 1px ridge #ff66ff;
}

.comment p {
  margin: 0;
  padding: 0;
}
</style>
