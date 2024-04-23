<!--実績値入力-->
<template>
  <v-app>
    <v-container>
      <v-dialog v-model="isDialog" width="600" persistent>
        <v-card>
          <div ref="guideComment" class="comment">
            <p>入力後リターンキーを押してください。</p>
          </div>
          <v-card-title v-if="title != null">実績値入力</v-card-title>
          <v-card-text>(PAMで計測した「f」と「g」の値を入力してください。)</v-card-text>
          <!-- タイトル部分 -->
          <input-header ref="titleHeader" />
          <!-- 入力部分 -->
          <v-container>
            <div class="text-subtitle-1">イールド値モデルパラメータ</div>
            <p v-if="!isAllValueInputted" class="error">{{this.messages.NotNumberAll}}</p>
            <div style="height: 420px">
              <div style="height: 420px; box-sizing: border-box">
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
            </div>
          </v-container>

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
        </v-card>
        <div class="datePicker" ref="dateInput">
          <v-date-picker
            v-model="dateInfo.date"
            :min="dateInfo.minDate"
            :max="dateInfo.maxDate"
            @change="handleChangeDate"
            locale="jp-ja"
          ></v-date-picker>
        </div>
      </v-dialog>
    </v-container>
  </v-app>
</template>
<script>
import moment from "moment";
import { mdiExitToApp } from "@mdi/js";
import InputHeader from "./InputHeader.vue";

import {
  usePhotosynthesisValuesUpdate,
  usePhotosynthesisValuesDetail,
} from "@/api/TopStateGrowth/PEActualValueInput";
import { AgGridVue } from "ag-grid-vue";
import messages from "@/assets/messages.json";
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
// セルの背景色を設定
//* ============================================
var regexp = new RegExp(/^[-]?([1-9]\d*|0)(\.\d+)?$/);
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
  name: "PEActualValueInput",
  props: {
    shared /** MountController */: { required: true },
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
          width: 200,
        },
        {
          field: "f",
          colId: "f",
          singleClickEdit: true,
          headerName: "f値",
          editable: true,
          resizable: true,
          width: 100,
          cellStyle: (params) => {
            return setBackground(params, 2, params.data.f);
          },
        },
        {
          field: "g",
          colId: "g",
          singleClickEdit: true,
          headerName: "g値",
          editable: true,
          resizable: true,
          width: 100,
          cellStyle: (params) => {
            return setBackground(params, 4, params.data.g);
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
      selectedItems: null,
      value: "",
      date: moment().format("YYYY-MM-DD"),
      isDialog: false,
      title: "", // 選ばれたモデル種別
      field: {}, // 選ばれた圃場
      // selected: null,
      device: {},
      // params: [],
      path: mdiExitToApp,
      menu: false,
      year: 0,
      isUpdated: false,
      // headers: HEADERS,
      skelton: {
        deviceId: null,
        year: null,
        date: this.date,
        f: 0.6,
        g: -0.001,
      },
    };
  },

  components: {
    InputHeader,
    AgGridVue,
  },

  mounted() {
    this.shared.mount(this);
  },

  methods: {
    //* ============================================
    // 初期化処理
    //* ============================================
    initialize: function (data) {
      this.selectedItems = data.menu;
      this.$nextTick(
        function () {
          this.$refs.titleHeader.initialize(data.menu);
        }.bind(this)
      );

      //年度
      this.year = this.selectedItems.selectedYear.id;
      // タイトル
      this.title = data.title;
      // 圃場
      this.field = this.selectedItems.selectedField;
      // デバイス
      this.device = this.selectedItems.selectedDevice;
      this.isUpdated = false;

      //光合成推定実績取得
      usePhotosynthesisValuesDetail(this.device.id, this.year)
        .then((response) => {
          const ps_data = response["data"]["data"];
          this.dateInfo.minDate = ps_data.minDate;
          this.dateInfo.maxDate = ps_data.maxDate;
          this.skelton.date = ps_data.minDate;
          if (this.rowData[0].date == null) {
            this.rowData[0].date = ps_data.minDate;
            this.$set(this.rowData, 0, {
              ...this.rowData[0],
              date: ps_data.minDate,
            });
          }
          if (ps_data.values.length > 0) {
            this.rowData = ps_data.values;
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    setStatus(status, bool) {
      if (!bool) {
        this.buttonStatus = this.buttonStatus | status;
      } else {
        this.buttonStatus = (this.buttonStatus | status) - status;
      }
      return bool;
    },
    //* ============================================
    // 登録処理
    //* ============================================
    register: function () {
      for (const item of this.rowData) {
        item.deviceId = this.device.id;
        item.year = this.year;
      }
      //光合成推定実績値更新
      usePhotosynthesisValuesUpdate(this.rowData)
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
    handleDate(date) {
      this.date = date;
    },
    close: function () {
      this.isDialog = false;
      this.shared.onConclude(this.isUpdated);
    },
    //* ============================================
    // 日付設定処理
    //* ============================================
    handleChangeDate() {
      this.hideDatePicker();
      let params = this.dateInfo.params;
      // let date = this.rowData[params.rowIndex].date;
      //  const rowNode = this.gridApi.getRowNode(date);
      //  const newDate = this.dateInfo.date;
      this.rowData[params.rowIndex].date = this.dateInfo.date;
      // var newValues = Object.assign({}, this.rowData[params.rowIndex]);
      let rowIndex = params.rowIndex;
      this.$set(this.rowData, rowIndex, {
        ...this.rowData[rowIndex],
        date: this.dateInfo.date,
      });
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
    // row追加・削除
    //* ============================================
    onCellClicked(params) {
      console.log("sek", params);

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
    // リターンキーが押されたときの処理を記述
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
      if (params.column.colId == "f" || params.column.colId == "g") {
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
