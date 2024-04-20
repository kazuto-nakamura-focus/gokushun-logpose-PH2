<template>
  <v-app>
    <v-container>
      <template>
        <div id="input_comment" class="comment">
          <p>入力後リターンキーを押してください。</p>
        </div>
        <v-row v-if="mode == 'update'">
          <!-- 更新モードの時表示-->
          <v-col align="right">
            <v-icon right icon="mdi-vuetify" @click="deleteDeviceInfo()">mdi-trash-can-outline</v-icon>
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="6">
            <div style="margin-bottom:12px;">
              <v-text-field
                label="デバイス名【必須】"
                dense
                hide-details="auto"
                outlined
                filled
                background-color="#F4FCE0"
                v-model.trim="deviceInfoData.name"
              ></v-text-field>
              <p v-if="!isDeviceNotNull" class="error">{{this.messages.required}}</p>
            </div>
            <div>
              <v-select
                label="圃場【必須】"
                v-bind:items="useFieldInfoDataList"
                dense
                outlined
                @change="setField"
                v-model="deviceInfoData.fieldId"
                item-text="name"
                item-value="id"
                return-object
                background-color="#F4FCE0"
                style="margin:0;"
              ></v-select>
              <p v-if="!isFieldNotNull" class="error">{{this.messages.required}}</p>
            </div>
            <div style="margin-bottom:12px;">
              <v-text-field
                label="品種"
                dense
                hide-details="auto"
                outlined
                filled
                background-color="#F4FCE0"
                v-model.trim="deviceInfoData.brand"
              ></v-text-field>
            </div>
            <div style="margin-bottom:12px;">
              <v-text-field
                label="Sigfox Device ID【必須】"
                dense
                hide-details="auto"
                outlined
                filled
                background-color="#F4FCE0"
                v-model.trim="deviceInfoData.sigFoxDeviceId"
              ></v-text-field>
              <p v-if="!isSigFoxNotNull" class="error">{{this.messages.required}}</p>
            </div>
          </v-col>
          <v-col cols="6">
            <div style="border-left:2px dotted gray;padding-left:10px;" class="deviceSet">
              <div style="margin-bottom:20px;">
                <v-text-field
                  label="基準日【必須】"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  default="01/01"
                  background-color="#F4FCE0"
                  v-model.trim="deviceInfoData.baseDateShort"
                ></v-text-field>
                <p v-if="!isBaseDateNotNull" class="error">{{this.messages.wrongDate}}</p>
              </div>
              <div>
                <v-select
                  v-model="deviceInfoData.timeZone"
                  :items="timeZone"
                  label="タイムゾーン【必須】"
                  width="60"
                  item-text="name"
                  background-color="#F4FCE0"
                  dense
                ></v-select>
                <p v-if="!isTzNotNull" class="error">{{this.messages.required}}</p>
              </div>
              <div style="margin-bottom:16px;">
                <v-text-field
                  label="運用開始日"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  default="01/01"
                  background-color="#F4FCE0"
                  v-model.trim="deviceInfoData.opStartShort"
                ></v-text-field>
              </div>

              <div style=" display: flex;margin-bottom:10px">
                <div style="width:250px;">
                  <v-select
                    label="引継ぎデバイス"
                    v-bind:items="deviceList"
                    @change="setDevice"
                    dense
                    outlined
                    v-model="deviceInfoData.prevDeviceId"
                    item-text="name"
                    item-value="id"
                    return-object
                    background-color="#F4FCE0"
                    style="margin:0;"
                  ></v-select>
                </div>
                <div style="width:230px;margin-left:15px">
                  <v-checkbox v-model="transitFlag" label="更新時実績値の引継ぎ実行"></v-checkbox>
                </div>
              </div>
              <div style="margin-bottom:16px;">
                <v-text-field
                  label="運用終了日"
                  dense
                  hide-details="auto"
                  outlined
                  filled
                  default="01/01"
                  background-color="#F4FCE0"
                  v-model.trim="deviceInfoData.opEndShort"
                ></v-text-field>
              </div>
            </div>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="12">
            <!-- *********** センサー情報 ********** -->

            <div>センサー</div>
            <div>
              <p v-if="!isAllValueInputted" class="error">{{this.messages.NotFilledAll}}</p>
            </div>
            <div style="height: 420px">
              <div style="height: 420px; box-sizing: border-box">
                <AgGridVue
                  style="width: 100%; height: 100%"
                  class="ag-theme-gs"
                  :columnDefs="columnDefs"
                  @grid-ready="onGridReady"
                  :rowData="rowData"
                  :gridOptions="gridOptions"
                  sizeColumn
                  @cell-clicked="onCellClicked"
                  @first-data-rendered="onRendered"
                  @cell-value-changed="onColumnValueChanged"
                  @cellMouseOver="onCellMouseOver"
                  @cellFocused="onCellFocused"
                  @cellKeyPress="onCellKeyPress"
                ></AgGridVue>
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
              :disabled="(buttonStatus!=0)||(!isAllValueInputted)"
            >{{ label }}</v-btn>
            <v-btn
              v-if="deviceInfoData.id != null"
              color="primary"
              class="ma-2 white--text"
              elevation="2"
              @click="dataLoad()"
              :disabled="(buttonStatus!=0)||(!isAllValueInputted)"
            >センサーデータのロード</v-btn>

            <v-btn color="gray" class="ma-2 black--text" elevation="2" @click="back()">キャンセル</v-btn>
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
//import moment from "moment-timezone";
import "moment/locale/ja";
import WaitDialog from "@/components-v1/parts/dialog/WaitDialog.vue";
import messages from "@/assets/messages.json";

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
//* -----------------------------------------------
// セルの背景色を設定する
//* -----------------------------------------------
// * 通常のセル
function setBackground(params, number, value) {
  //
  if (value == null || value.length == 0) {
    setStatus(params, number, false);
    return { border: "2px solid #f33" };
  } else {
    setStatus(params, number, true);
    return { border: "1px solid #fff" };
  }
}
// * 樹液流のセル
function setSapBackground(params, number, displayId, value) {
  let obj = { isActive: false };
  if (params.column.colDef.inactive === undefined) {
    params.column.colDef.inactive = new Map();
  }
  if (params.column.colDef.inactive[params.rowIndex] === undefined) {
    params.column.colDef.inactive[params.rowIndex] = obj;
  } else {
    obj = params.column.colDef.inactive[params.rowIndex];
  }
  if (!(displayId == null || displayId == "4")) {
    setStatus(params, number, true);
    obj.isActive = false;
    return { border: "1px solid #fff", backgroundColor: "#aaa" };
  } else {
    obj.isActive = true;
    if (value == null || value.length == 0) {
      setStatus(params, number, false);
      return { border: "2px solid #f33", backgroundColor: "inherit" };
    } else {
      setStatus(params, number, true);
      return { border: "1px solid #fff", backgroundColor: "inherit" };
    }
  }
}
//* -----------------------------------------------
// 行のエラーステータスを設定する
//* -----------------------------------------------
function setStatus(params, number, bool) {
  let row = params.node.parent.allLeafChildren[params.rowIndex];
  if (row.status === undefined) {
    row.status = new Number(0);
  }
  if (bool) row.status = (row.status | number) - number;
  else row.status = row.status | number;
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
    deviceList: Array, //デバイスリスト
  },

  data() {
    return {
      messages: messages,
      errormessage: "",
      isAllValueInputted: true,
      timeZone: [],
      label: this.mode == "update" ? "更新" : "追加",
      transitFlag: false,
      columnDefs: [
        {
          field: "displayId",
          headerName: "センサータイプ",
          singleClickEdit: true,
          resizable: true,
          editable: true,
          width: 150,
          cellEditor: "agSelectCellEditor",
          cellEditorParams: {
            values: this.extractKeys(
              this.$options.propsData.useDeviceMasters.sensorContents
            ),
          },
          refData: this.useDeviceMasters.sensorContents,
          valueListGap: 0,
          cellStyle: (params) => {
            return setBackground(params, 1, params.data.displayId);
          },
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
          cellStyle: (params) => {
            return setBackground(params, 2, params.data.modelId);
          },
        },
        {
          field: "channel",
          headerName: "チャンネル",
          singleClickEdit: true,
          resizable: true,
          editable: true,
          width: 90,
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
          },
          valueListGap: 0,
          cellStyle: (params) => {
            return setBackground(params, 4, params.data.channel);
          },
        },
        {
          field: "name",
          colId: "sensor",
          singleClickEdit: true,
          headerName: "センサ―名",
          editable: true,
          resizable: true,
          width: 100,
          cellStyle: (params) => {
            return setBackground(params, 8, params.data.name);
          },
        },
        {
          field: "sizeId",
          singleClickEdit: true,
          headerName: "サイズ",
          resizable: true,
          width: 100,
          cellEditor: "agSelectCellEditor",
          cellEditorParams: {
            values: this.extractKeys(
              this.$options.propsData.useDeviceMasters.sensorSizes
            ),
          },
          refData: this.useDeviceMasters.sensorSizes,
          valueListGap: 0,
          editable: (params) =>
            params.data.displayId == "4" || params.data.displayId == null,
          cellStyle: (params) => {
            return setSapBackground(
              params,
              16,
              params.data.displayId,
              params.data.sizeId
            );
          },
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
            return setSapBackground(
              params,
              32,
              params.data.displayId,
              params.data.kst
            );
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
            return setSapBackground(
              params,
              64,
              params.data.displayId,
              params.data.stemDiameter
            );
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
      deviceInfoData: this.useDeviceInfoData,
      sensorList: null,
      // sensorData: null, //マスターセンサーデータ
      buttonStatus: 0,
      mouseX: 0,
      mouseY: 0,
    };
  },

  components: {
    AgGridVue,
    WaitDialog,
  },
  beforeMount() {
    if (null == this.deviceInfoData) {
      this.deviceInfoData = {
        //* 圃場ID
        fieldId: null,
        //* 圃場名
        field: null,
        //* 品種ID
        brandId: null,
        //* 品種
        brand: null,
        //* タイムゾーン
        timeZone: null,
        //* 基準日
        baseDate: null,
        //* 基準日表示用
        baseDateShort: null,
        //* 登録日時
        registeredDate: null,
        // * SigFox ID
        sigFoxDeviceId: null,
        // * 引継ぎ元デバイス
        prevDeviceId: null,
        // * 運用開始日
        opStart: null,
        // * 運用終了日
        opEnd: null,
        // * 運用開始日
        opStartShort: null,
        // * 運用終了日
        opEndShort: null,
        sensorItems: [],
      };
    }
  },
  mounted() {
    this.timeZone.push("Asia/Tokyo");
    this.timeZone.push("Pacific/Auckland");
  },
  computed: {
    //* -------------------------------------------
    // 入力チェック
    //* -------------------------------------------
    // * デバイス名
    isDeviceNotNull() {
      return this.setStatus(
        1,
        this.deviceInfoData.name != null && this.deviceInfoData.name.length != 0
      );
    },
    // Sigfox ID
    isSigFoxNotNull() {
      return this.setStatus(
        2,
        this.deviceInfoData.sigFoxDeviceId != null &&
          this.deviceInfoData.sigFoxDeviceId.length != 0
      );
    },
    // 基準日
    isBaseDateNotNull() {
      if (null == this.deviceInfoData.baseDateShort)
        return this.setStatus(4, false);
      if (0 == this.deviceInfoData.baseDateShort.length)
        return this.setStatus(4, false);
      let result = this.deviceInfoData.baseDateShort.match(
        /^(0[1-9]|1[0-2])\/(0[1-9]|[12][0-9]|3[01])$/
      );
      return this.setStatus(4, null != result);
    },
    // 圃場
    isFieldNotNull() {
      return this.setStatus(8, this.deviceInfoData.fieldId != null);
    },
    // タイムゾーン
    isTzNotNull() {
      return this.setStatus(16, this.deviceInfoData.timeZone != null);
    },
  },
  methods: {
    setStatus(status, bool) {
      if (!bool) {
        this.buttonStatus = this.buttonStatus | status;
      } else {
        this.buttonStatus = (this.buttonStatus | status) - status;
      }
      return bool;
    },
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
    setDevice(item) {
      this.deviceInfoData.prevDeviceId = item.id;
    },
    //* ============================================
    // セルの値が変化した場合
    //* ============================================
    onColumnValueChanged: function (param) {
      //既存のデータ（ノード）を格納
      const node = param?.node;

      //* 樹液流の場合
      //Display IDが4以外（「樹液流」以外」）の場合、設定されているsizeId・kst・stemDiameterをNullに設定
      //Null設定することで、セルを更新する際に空欄になる。
      if ("4" != param.data.displayId) {
        const nodeData = node?.data;
        nodeData.sizeId = null;
        nodeData.kst = null;
        nodeData.stemDiameter = null;
      }

      //該当行の特定カラムを再度更新する。
      //更新するセルのスタイルは、columnDefで定義したCellStyleを基づき、スタイルを更新する。
      const refreshParams = {
        force: true,
        columns: ["sizeId", "kst", "stemDiameter"],
        rowNodes: [node],
      };
      param.api.refreshCells(refreshParams);

      let items = param.node.parent.allLeafChildren;
      this.isAllValueInputted = true;
      for (const item of items) {
        if (0 !== item.status) {
          this.isAllValueInputted = false;
          break;
        }
      }
    },
    update: function () {
      const message =
        this.mode == "update"
          ? "更新してもよろしいですか？"
          : "登録してもよろしいですか？";
      const deviceId = this.mode == "update" ? this.deviceInfoData.id : null;
      if (confirm(message)) {
        const rowData = [];
        this.gridApi.forEachNode((node) => {
          rowData.push(node.data);
        });

        // センサー情報を追加する
        let sensorItems = [];
        // 画面のセンサー情報のリストを取り出す
        for (const item of rowData) {
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
          prevDeviceId: this.deviceInfoData.prevDeviceId,
          opStartShort: this.deviceInfoData.opStartShort,
          opEndShort: this.deviceInfoData.opEndShort,
          transitFlag: this.transitFlag,
          //センサー情報
          sensorItems: sensorItems,
        };

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
      this.$refs.wait.start("センサーデータをアップデート中です。", true);
      useLoadData(this.deviceInfoData.id)
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

      if (this.deviceInfoData.sensorItems.length === 0) {
        this.gridApi.forEachNode((node) => this.rowData.push(node.data));
        const rowDataArray = [...this.rowData];
        console.log("rowDataArray", rowDataArray);
      } else {
        this.rowData = [...this.deviceInfoData.sensorItems];
        console.log("MSEditDevice_rowData(3):", this.rowData);
      }
    },

    //row追加・削除
    //this.rowDataを利用せず、リアルタイムでAggridテーブルからデータを取得して、追加・削除制御を行う。
    onCellClicked(params) {
      const gridApi = params?.api;
      const nodes = [];
      gridApi?.forEachNode((node) => nodes.push(node));
      if (params.column.colId === "remove") {
        params.api.applyTransaction({
          remove: [params.data],
        });

        if (nodes.length == 1) {
          let row = Object.assign({}, this.skelton);
          params.api.applyTransaction({
            add: [row],
          });
        }
      } else if (params.column.colId === "add") {
        let row = Object.assign({}, this.skelton);
        params.api.applyTransaction({
          add: [row],
        });
      }
    },
    onRendered() {
      this.gridApi.forEachNode((node) => {
        if (node.status !== undefined && 0 != node.status) {
          this.isAllValueInputted = false;
        }
        //console.log(node);
      });
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
      // マウスの座標を取得
      this.mouseX = params.event.clientX;
      this.mouseY = params.event.clientY;
    },
    //* ============================================
    //  セルにフォーカスが移ったときコメントを表示
    //* ============================================
    onCellFocused(params) {
      // * セルが編集可能で通常入力
      if (params.column.colId == "sensor") {
        this.showComment();
      } else if (
        params.column.colId == "kst" ||
        params.column.colId == "stemDiameter"
      ) {
        if (params.column.colDef.inactive[params.rowIndex].isActive) {
          this.showComment();
        } else {
          this.hideComment();
        }
      } else {
        this.hideComment();
      }
    },

    //* ============================================
    // マウスの座標軸上にコメントを表示
    //* ============================================
    showComment() {
      //* コメントボックスを取得
      let element = document.getElementById("input_comment");
      //* コメント位置を設定
      let y = this.mouseY + 10;
      let x = this.mouseX - 10;
      element.style.top = "" + y + "px";
      element.style.left = "" + x + "px";
      //* コメントを表示
      element.style.display = "inline-block";
      // * コメント表示時は入力不可
      this.setStatus(32, false);
    },
    //* ============================================
    // コメントを隠す
    //* ============================================
    hideComment() {
      let element = document.getElementById("input_comment");
      element.style.display = "none";
      this.setStatus(32, true);
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
.deviceSet {
}
.deviceSet .v-input--selection-controls {
  padding: 0;
  margin: 0;
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