<template>
  <v-app>
    <v-container class="spacing-playground pa-5" fluid>
      <ButtonSelector :titleWidth="100" :title="menuTitle" :data="menu" :handleClick="handleClickMenu"
        :multiple="false" />
      <v-row><v-divider /></v-row>
      <v-expand-transition>
        <div v-if="selectedMenu != null">
          <ButtonSelector :titleWidth="100" :title="grouthTitle" :data="filterGrouthList" :handleClick="handleClickGrouth"
            :multiple="true" />
          <v-row><v-divider /></v-row>
        </div>
      </v-expand-transition>

      <v-expand-transition>
        <div v-if="selectedMenu != null && selectedField.length > 0">
          <ButtonSelector :titleWidth="100" :title="deviceTitle" :data="masterDeviceList" :handleClick="handleClickDevice"
            :multiple="true" />
          <v-row><v-divider /></v-row>
        </div>
      </v-expand-transition>

      <v-expand-transition>
        <div v-if="selectedMenu != null && selectedDevices.length > 0">
          <ButtonSelector :titleWidth="100" :title="yearTitle" :data="masterYearList" :handleClick="handleClickYear"
            :multiple="true" />
          <v-row><v-divider /></v-row>
        </div>
      </v-expand-transition>

      <v-expand-transition>
        <div v-if="selectedMenu != null &&
          selectedMenu.variable != 'fruiting' &&
          selectedField.length > 0 &&
          selectedDevices.length > 0 &&
          selectedYears.length > 0
          ">
          <template>
            <v-container>
              <v-row class="mt-1 mb-1">
                <v-col>
                  <v-btn class="ml-1" v-for="(button, index) in editButtons" depressed color="primary" elevation="3"
                    :key="index" @click="openDialog(button)">
                    {{ button.name }}
                  </v-btn>
                </v-col>
              </v-row>
            </v-container>
          </template>
        </div>
      </v-expand-transition>

      <!-- 線グラフ -->
      <!-- chartDataには、ラベルやデータを挿入 -->
      <!-- chartOptionsには、チャートの設定を挿入 -->
      <!-- chartStyleには、チャートのとスタイル -->
      <!-- tickHeightは、Y軸の目盛り間隔の長さ（グラフの高さは、目盛りの間隔によって変わる） -->
      <!-- モックデータの「/src/assets/test/mockChartData.json」を参考にすること -->
      <!-- tickHeight設定を用いることで、高さ設定可能 -->
      <LineGraph v-if="selectedMenu != null &&
        selectedMenu.variable != 'fruiting' &&
        selectedField.length > 0 &&
        selectedDevices.length > 0 &&
        selectedYears.length > 0
        " ref="refLineGraph" :chartData="chartData" :chartOptions="chartOptions" :chartStyle="chartStyle"
        :tickHeight="100" />

      <div v-else-if="selectedMenu != null &&
        selectedMenu.variable == 'fruiting' &&
        selectedField.length > 0 &&
        selectedDevices.length > 0 &&
        selectedYears.length > 0
        ">
        <FVParameterSets v-if="selectedFieldOnly" ref="refFVParameterSets" :shared="sharedDetail" />
        <FVActualValueInput v-else ref="refFVActualValueInput" :shared="sharedDetail" />
      </div>
    </v-container>

    <div v-if="selectedMenu != null && selectedMenu.variable == 'stage'">
      <GEActualValueInput ref="refActualValueInput" :shared="sharedInput" />
      <GEParameterSets ref="refGEParameterSets" :shared="sharedDetail" />
      <ReferenceFValue ref="refFValueInput" :shared="sharedDetail" />
    </div>
    <div v-if="selectedMenu != null && selectedMenu.variable == 'area'">
      <LAActualValueInput ref="refActualValueInput" :shared="sharedInput" :selectedField="selectedField"
        :selectedDevices="selectedDevices" />
      <LAParameterSets ref="refGEParameterSets" :shared="sharedDetail" />
      <!-- <ReferenceFValue /> -->
    </div>
    <div v-if="selectedMenu != null && selectedMenu.variable == 'photosynthesis'">
      <PEActualValueInput ref="refActualValueInput" :shared="sharedInput" :selectedField="selectedField"
        :selectedDevices="selectedDevices" />
      <PEParameterSets ref="refGEParameterSets" :shared="sharedDetail" />
      <!-- <ReferenceFValue /> -->
    </div>
    <!-- handleSubmitParameterSetNameハンドラ関数も参照 -->
    <!-- isShowParameterSetNameは、表示の初期値 -->
    <ParameterSetName :handleSubmit="handleSubmitParameterSetName" :show="isShowParameterSetName" confirmText="保存"
      cancelText="取消" ref="refParameterSetName" />
  </v-app>
</template>

<script>
import moment from "moment";
import menu from "@/assets/menu.json";
// TODO マスタデータはMock
import masterGrouthData from "@/assets/test/masterGrouthData.json";
import masterDeviceList from "@/assets/test/masterDeviceList.json";
import totalData from "@/assets/test/totalData.json";
import masterYearList from "@/assets/years.json";
import mockChartData from "@/assets/test/mockChartData";

import displayOrder from "@/assets/displayOrder.json";
import allEditButtons from "./hooks/editButtons.json";
import { DeviceParser } from "@/lib/deviceParser.js";
import { DialogController, MountController } from "@/lib/mountController.js";
import LineGraph from "@/components/parts/LineGraph";
import GEActualValueInput from "./GEActualValueInput";
import GEParameterSets from "./GEParameterSets";
import ReferenceFValue from "./ReferenceFValue";
import FVActualValueInput from "./FVActualValueInput";
import FVParameterSets from "./FVParameterSets";
import LAActualValueInput from "./LAActualValueInput";
import LAParameterSets from "./LAParameterSets";
import PEActualValueInput from "./PEActualValueInput";
import PEParameterSets from "./PEParameterSets";
import ButtonSelector from "@/components/parts/Ph2ButtonSelector";
import { TopDataParser } from "@/lib/topDataParser";
import ParameterSetName from "@/components/parts/ParameterSetName";

//グラフ関連
import mockGraphDataDto from "@/assets/mock/graphDataDto";
import topStageGrowthGraph from "@/assets/format/topStageGrowthGraph";

export default {
  data() {
    return {
      sourceData: [],
      fields: [],
      selectedMenu: null,
      selectedField: [],
      selectedYears: [],
      selectedDevices: [],
      dates: [],
      sharedMenu: new MountController(),
      sharedField: new MountController(),
      sharedModels: new DialogController(), // モデル式
      menu: menu,
      fieldItems: [],
      DeviceParser: new DeviceParser(),
      masterGrouthData: masterGrouthData,
      index: 0,
      //ここから、ユーが追加
      displayOrder: displayOrder,
      totalData: totalData,
      sharedInput: new DialogController(),
      sharedDetail: new DialogController(),
      TopDataParser: new TopDataParser(),
      menuTitle: "モデル",
      grouthTitle: "圃場",
      yearTitle: "年度",
      deviceTitle: "デバイス",
      parameterSetTitle: "パラメータセット編集",
      masterDeviceList: masterDeviceList,
      masterYearList: masterYearList,
      editButtons: [],
      filterGrouthList: [],
      yearList: [],
      parameterSetController: new DialogController(),
      //複数選択確認
      selectedFieldOnly: false,
      chartData: mockChartData["data"],
      chartOptions: mockChartData["options"],
      chartStyle: {
        // height: "500px",
      },
      isShowParameterSetName: false,
    };
  },
  beforeMount() {
    // const chartOptions = this.chartOptions;
    // const annotation = chartOptions["annotation"];
    // const annotations = annotation["annotations"];
    // annotations[0].value = "2023-01-31";
    // console.log(annotations);
  },
  mounted() {
    //------------------------------------------------------------
    // this.$nextTick(function () {
    //   // メニューエリアの設定
    //   this.sharedMenu.setUp(
    //     this.$refs.MenuArea,
    //     function (MenuArea) {
    //       MenuArea.initialize("モデル", this.menu);
    //       this.setSelectedMenu();
    //       this.setSelectedList();
    //       this.$refs.di.isDisabled =
    //         this.selectedMenu == null || this.selectedField == null;
    //     }.bind(this),
    //     function () {
    //       this.setSelectedMenu();
    //       this.setSelectedList();
    //       this.$refs.di.isDisabled =
    //         this.selectedMenu == null || this.selectedField == null;
    //     }.bind(this)
    //   );
    //   // 圃場エリアの設定
    //   this.sharedField.setUp(
    //     this.$refs.FieldArea,
    //     function (FieldArea) {
    //       FieldArea.initialize("圃場", this.sourceData);
    //       this.setSelectedList();
    //       this.$refs.di.isDisabled =
    //         this.selectedMenu == null || this.selectedField == null;
    //     }.bind(this),
    //     function () {
    //       this.setSelectedList();
    //       this.$refs.di.isDisabled =
    //         this.selectedMenu == null || this.selectedField == null;
    //     }.bind(this)
    //   );
    // });
  },
  updated() { },
  components: {
    // GraphArea,
    LineGraph,
    GEActualValueInput,
    GEParameterSets,
    // GEFlValue,
    ReferenceFValue,
    LAActualValueInput,
    LAParameterSets,
    PEActualValueInput,
    PEParameterSets,
    FVActualValueInput,
    FVParameterSets,
    ButtonSelector,
    ParameterSetName,
  },
  created: function () {
    //*** API呼び出し */
    // totalData=検知データ
    // chartData=グラフ表示設定
    // chartOptions=グラフ表示オプション設定
    // graphData=グラフ表示データ
    // masterDeviceList=デバイスリストのマスタ
    // masterYearList=年度リストのマスタ

    console.log(this.displayOrder);
    this.sourceData = this.TopDataParser.parse(
      this.displayOrder.labels,
      this.totalData
    );
    for (const item of this.sourceData) {
      item.visible = new Object();
      item.visible = true;
      this.displayData.push(item);
    }
    console.log(this.sourceData);
    // console.log("store", this.$store.getters.sourceData);
    // this.sourceData = this.$store.getters.sourceData;
    // this.setSelectedList();
    // this.setSelectedMenu();
    // console.log("top_stage_graph");
    // console.log(this.sourceData);
    const today = moment().format("YYYY-MM-DD");
    this.dates.push(today);
    this.dates.push(today);
  },
  methods: {
    setSelectedList: function () {
      this.selectedField = [];
      for (const item of this.sourceData) {
        if (item.state) {
          this.selectedField.push(item);
        }
      }
      console.log("setSelectedList", this.sourceData);
      if (this.selectedField.length == 0) this.selectedField = [];
    },
    setSelectedMenu: function () {
      this.selectedMenu = null;
      for (const item of this.menu) {
        if (item.state) {
          this.selectedMenu = item;
          return;
        }
      }
    },
    setData: function (dates) {
      // 送信データ
      var dataList = [];
      var key = this.selectedMenu.title;
      // 選択された各フィールドに対して
      for (const item of this.selectedField) {
        //  if(!item.state) continue;
        // マスターデータから一致するデータを取得
        var list = this.masterGrouthData[this.selectedMenu.title];
        for (const master of list) {
          if (item.title == master.name) {
            key = key + "_" + item.title;
            var grData = { label: "", borderColor: "", data: [] };
            grData.label = item.title;
            grData.borderColor = master.borderColor;
            grData.data = master.data;
            grData.fill = false;
            grData.type = "line";
            grData.lineTension = 0.3;
            dataList.push(grData);
            break;
          }
        }
      }
      this.dates = dates;
      var graphTitile = "(" + this.dates[0] + "～" + this.dates[1] + ")";
      this.$refs.gr.updateGraph(
        key,
        this.selectedMenu.title,
        graphTitile,
        dataList
      );
    },
    setGraphData: function () {
      // 送信データ
      var dataList = [];
      const selectedMenu = this.selectedMenu;
      // const selectedField = this.selectedField;
      // const filterGrouthList = this.filterGrouthList;
      // const selectedDevice = this.selectedDevice;
      const selectedYears = this.selectedYears;
      var key = selectedMenu["title"];
      // 選択された各フィールドに対して
      for (const item of this.selectedField) {
        //  if(!item.state) continue;
        // マスターデータから一致するデータを取得
        var list = this.masterGrouthData[key];
        for (const master of list) {
          if (item.title == master.name) {
            key = key + "_" + item.title;
            var grData = { label: "", borderColor: "", data: [] };
            grData.label = item.title;
            grData.borderColor = master.borderColor;
            grData.data = master.data;
            grData.fill = false;
            grData.type = "line";
            grData.lineTension = 0.3;
            dataList.push(grData);
            break;
          }
        }
      }
      var graphTitile = `${selectedYears[0]}`;
      this.$refs.gr.updateGraph(
        key,
        this.selectedMenu.title,
        graphTitile,
        dataList
      );
    },
    openDialog: function (item) {
      const key = item.key;
      const selectedData = {
        title: this.selectedMenu.title,
        fields: this.sourceData,
        params: this.selectedMenu.params,
        dates: this.dates,
      };
      if (key == "edit_achievements") {
        console.log("edit_achievements");
        this.$refs.refActualValueInput.initialize(selectedData);
        this.sharedInput.setUp(
          this.$refs.refActualValueInput,
          function () {
            this.$refs.refActualValueInput.initialize(selectedData);
          }.bind(this),
          function () {
            this.setData();
          }.bind(this)
        );
      } else if (key == "edit_f_table") {
        console.log("edit_f_table");
        this.$refs.refFValueInput.initialize(selectedData);
        this.sharedInput.setUp(
          this.$refs.refFValueInput,
          function () {
            this.$refs.refFValueInput.initialize(selectedData);
          }.bind(this),
          function () {
            this.setData();
          }.bind(this)
        );
      } else if (key == "edit_parameter_sets") {
        this.$refs.refGEParameterSets.initialize(selectedData);
        this.sharedInput.setUp(
          this.$refs.refGEParameterSets,
          function () {
            this.$refs.refGEParameterSets.initialize(selectedData);
          }.bind(this),
          function () {
            this.setData();
          }.bind(this)
        );
        // this.sharedDetail.setUp(
        //   this.$refs.refGEParameterSets,
        //   function (md) {
        //     md.initialize(selectedData, this.selectedItem);
        //   }.bind(this),
        //   function (model, save) {
        //     if (save) this.saveItem(model);
        //     else {
        //       this.deleteItem(model);
        //     }
        //   }.bind(this)
        // );
      }
    },
    handleClickMenu: function (item, index, active) {
      active == true
        ? ((this.selectedMenu = item),
          this.$store.dispatch("changeModel", item.title))
        : ((this.selectedMenu = null),
          this.$store.dispatch("changeModel", null));
      this.selectedField = [];
      this.selectedYears = [];
      if (this.selectedMenu != null) {
        const title = this.selectedMenu.title;
        const variable = this.selectedMenu.variable;
        const filterGrouthList = this.masterGrouthData[title];
        const editButtons = allEditButtons[variable].buttons;
        this.filterGrouthList.splice(0);
        this.filterGrouthList.push(...filterGrouthList);
        this.editButtons.splice(0);
        this.editButtons.push(...editButtons);
        console.log(this.filterGrouthList);
      } else {
        this.selectedField.splice(0);
        this.filterGrouthList.splice(0);
        this.selectedYears.splice(0);
        this.selectedDevice.splice(0);
      }
    },
    handleClickGrouth: function (item, index, active) {
      if (active) {
        this.selectedField.push(item);
        this.$store.dispatch("changeFields", { data: item, mode: true });
      } else {
        if (this.selectedField.includes(item)) {
          this.selectedField.splice(this.selectedField.indexOf(item), 1);
          this.$store.dispatch("changeFields", { data: item, mode: false });
        }
      }

      // if (this.selectedField.length > 1) {
      //   this.refFVParameterSetsChange();
      //   this.selectedFieldOnly = true;
      // } else {
      //   this.refFVActualValueInputChange();
      //   this.selectedFieldOnly = false;
      // }
    },
    handleClickDevice: function (item, index, active) {
      if (active) {
        this.selectedDevices.push(item);
        this.$store.dispatch("changeDevices", { data: item, mode: true });
      } else {
        if (this.selectedDevices.includes(item)) {
          this.selectedDevices.splice(this.selectedDevices.indexOf(item), 1);
          this.$store.dispatch("changeDevices", { data: item, mode: false });
        }
      }
      if (this.selectedDevices.length > 1) {
        this.refFVParameterSetsChange();
        this.selectedFieldOnly = true;
      } else {
        this.refFVActualValueInputChange();
        this.selectedFieldOnly = false;
      }
    },
    handleClickYear: function (item, index, active) {
      if (active) {
        this.selectedYears.push(item);
        this.$store.dispatch("changeYears", { data: item, mode: true });
      } else {
        if (this.selectedYears.includes(item)) {
          this.selectedYears.splice(this.selectedYears.indexOf(item), 1);
          this.$store.dispatch("changeYears", { data: item, mode: false });
        }
      }
      if (this.selectedMenu.variable == "fruiting") {
        this.refFVParameterSetsChange();
        if (this.selectedYears.length == 1) {
          this.refFVActualValueInputChange();
        }
      }
      this.setGraphData();
    },
    handleSubmit: function (name) {
      //パラメータセット名の外部から制御
      console.log(name);
      //何らかのAPIの処理を完了させた後に、refを用いてダイアログをクローズする
      //Reactでは、親コンポーネントからのパラメータを修正すると子コンポーネントにも反映されるが、
      //Vueでは、子コンポーネントに反映されないため、ref機能を使用して子コンポーネント内関数を呼ぶ必要がある。
      this.$refs.refParameterSetName.close();
    },
    //着果量パラメータセット
    refFVParameterSetsChange: function () {
      const refFVParameterSets = this.$refs.refFVParameterSets;
      if (refFVParameterSets) {
        refFVParameterSets.updateTable();
      }
    },
    //着果量実績値
    refFVActualValueInputChange: function () {
      const refFVActualValueInput = this.$refs.refFVActualValueInput;
      if (refFVActualValueInput) {
        refFVActualValueInput.updateTable();
      }
    },

    updateChartData: function () {
      //--------------------------------------------------
      //グラフのモックデータ
      //--------------------------------------------------
      //GraphDataDTO内のSelectedTarget型
      const graphData = mockGraphDataDto;

      //グラフ描画フォーマット格納
      const chartData = topStageGrowthGraph["data"];
      //-------------------------------------------------

      // const prevLabels = chartData["labels"];
      const datasets = chartData["datasets"];

      const actualValueList = datasets.filter((data) => {
        return data["label"] == "実績";
      });

      const estimateValueList = datasets.filter((data) => {
        return data["label"] == "推定";
      });

      const actualValue =
        actualValueList.length > 0 ? actualValueList[0] : null;
      const estimateValue =
        estimateValueList.length > 0 ? estimateValueList[0] : null;

      const presentYear = graphData["selected"].year;
      const prevYear = presentYear - 1;
      const date = new Date(prevYear, 11, 1);

      const labels = [];
      for (let i = 0; i <= 13; i++) {
        labels.push(date.toLocaleDateString());
        date.setMonth(date.getMonth() + 1);
      }

      const years = graphData["years"];

      const actualDatasets = [];
      const estimateDatasets = [];
      for (let i = 0; i < years.length; i++) {
        //「実績」データを実績配列に挿入
        actualDatasets.push(years[i]);
        //「実績」データ数の分、推定配列に空のデータを挿入
        if (i != years.length - 1) estimateDatasets.push(NaN);
      }

      for (let i = 0; i <= 14 - actualDatasets.length; i++) {
        //「推定」データを推定配列に挿入
        estimateDatasets.push(i * 10);
      }

      //新しく生成したラベル配列をセット
      chartData["labels"] = labels;

      //実績配列をセット
      actualValue["data"] = actualDatasets;
      //推定配列をセット
      estimateValue["data"] = estimateDatasets;

      //データセットをセット
      chartData["datasets"] = [actualValue, estimateValue];

      //データセットを基づき、グラフ更新
      this.$refs.refLineGraph.updateData(chartData);

      //グラフデータ
      //生育推定モデルグラフデータ取得（未使用）
      //useGrowthGraphByParamSet
      //生育推定実績グラフデータ取得（未使用）
      //useGrowthGraphReal
      //生育推定イベントデータ取得（未使用）
      //useGrowthEvent

      //葉面積モデルグラフデータ取得（未使用）
      //useLeafGraphAreaByParamSet
      //葉面積実績グラフデータ取得（未使用）
      //useLeafGraphAreaReal
      //葉枚数モデルグラフデータ取得（未使用）
      //useLeafGraphCountByParamSet
      //葉枚数実績グラフデータ取得（未使用）
      //useLeafGraphCountReal
      
      //光合成推定グラフデータ取得（未使用）
      //usePhotosynthesisGraphByParamSet
      //光合成推定実績グラフデータ取得（未使用）
      //usePhotosynthesisGraphReal
    },
  },
};
</script>

<style lang="scss">
// ヘッダー部
.fields {
  display: flex;
  padding: 3pt;
  font-size: 11pt;
  width: 90%;
  flex-wrap: wrap;
}

.menu {
  width: 90%;
  display: flex;
  justify-content: space-strech;
  flex-wrap: wrap;
}

.date {
  //display: flex;
  font-size: 11pt;
  // width: 80%;
  //justify-content: space-strech;
  //flex-wrap: wrap;
}
</style>
