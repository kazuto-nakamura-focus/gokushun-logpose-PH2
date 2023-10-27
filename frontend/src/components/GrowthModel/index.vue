<template>
  <v-app>
    <v-container class="spacing-playground pa-5" fluid>
      <targetMenu
        ref="targetMenu"
        :shared="sharedMenu"
        :model="isModel"
      ></targetMenu>
      <div v-if="bodyStatus">
        <template>
          <v-container>
            <v-row class="mt-1 mb-1">
              <v-col>
                <v-btn
                  class="ml-1"
                  v-for="(button, index) in editButtons"
                  depressed
                  color="primary"
                  elevation="3"
                  :key="index"
                  @click="openDialog(button)"
                >
                  {{ button.name }}
                </v-btn>
              </v-col>
            </v-row>
          </v-container>
        </template>
      </div>
      <ph2GraphContainer
        ref="gfa"
        v-show="bodyStatus && selectedMenu.selectedModel.id != 4"
      ></ph2GraphContainer>

      <div
        v-if="isFVDisplayed"
        v-show="bodyStatus && selectedMenu.selectedModel.id == 4"
      >
        <FVActualValueInput ref="refFVActualValueInput" />
      </div>
    </v-container>

    <GEActualValueInput ref="refGEActualValueInput" :shared="sharedParam[0]" />
    <ReferenceFValue ref="refReferenceFValue" :shared="sharedParam[1]" />
    <parmeter-set-dialog
      ref="refGEParameterSets"
      :shared="sharedParam[2]"
      :modelId="growthModel"
    />

    <LAActualValueInput
      ref="refLAActualValueInput"
      :shared="sharedParam[3]"
      :selectedField="selectedField"
      :selectedDevices="selectedDevices"
    />
    <parmeter-set-dialog
      ref="refLAParameterSets"
      :shared="sharedParam[4]"
      :modelId="leafModel"
    />
    <PEActualValueInput
      ref="refPEActualValueInput"
      :shared="sharedParam[5]"
      :selectedField="selectedField"
      :selectedDevices="selectedDevices"
    />
    <parmeter-set-dialog
      ref="refPEParameterSets"
      :shared="sharedParam[6]"
      :modelId="photoModel"
    />
  </v-app>
</template>

<script>
import moment from "moment";
import targetMenu from "@/components/parts/Ph2TargetMenu.vue";

import ph2GraphContainer from "@/components/GrowthModel/Ph2GraphContainer.vue";
import allEditButtons from "@/components/TopStageGrowth/hooks/editButtons.json";
import { DeviceParser } from "@/lib/deviceParser.js";
import { DialogController, MountController } from "@/lib/mountController.js";
import GEActualValueInput from "@/components/TopStageGrowth/actualValueInput/GEMainInput.vue";
import ParmeterSetDialog from "@/components/TopStageGrowth/ParameterSet/ParmeterSetDialog.vue";
import ReferenceFValue from "@/components/TopStageGrowth/ReferenceFValue";
import FVActualValueInput from "@/components/TopStageGrowth/actualValueInput/FVInput.vue";
import LAActualValueInput from "@/components/TopStageGrowth/actualValueInput/LAInput.vue";
import PEActualValueInput from "@/components/TopStageGrowth/actualValueInput/PEInput.vue";
import { TopDataParser } from "@/lib/topDataParser";

export default {
  data() {
    return {
      // 定数
      isModel: true,
      growthModel: 1,
      leafModel: 2,
      photoModel: 3,
      bodyStatus: false,
      sourceData: [],
      fields: [],
      selectedMenu: null,
      selectedField: [],
      selectedYears: [],
      selectedDevices: [],
      dates: [],
      sharedMenu: new MountController(),
      sharedParam: [
        new DialogController(),
        new DialogController(),
        new DialogController(),
        new DialogController(),
        new DialogController(),
        new DialogController(),
        new DialogController(),
        new DialogController(),
      ],
      fieldItems: [],
      DeviceParser: new DeviceParser(),
      index: 0,
      //ここから、ユーが追加
      TopDataParser: new TopDataParser(),
      editButtons: [],
      filterGrouthList: [],
      yearList: [],
      parameterSetController: new DialogController(),
      //複数選択確認
      selectedFieldOnly: false,
      // グラフ表示エリアのフラグ
      isgraphDisplayed: false,
      // 着果負担エリアの初期化フラグ
      isFVDisplayed: false,
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
          this.selectedMenu = selected;
          // * ボタンの表示
          const editButtons =
            allEditButtons[this.selectedMenu.selectedModel.id].buttons;
          this.editButtons.splice(0);
          this.editButtons.push(...editButtons);
          if (this.selectedMenu.selectedModel.id != 4) {
            this.$nextTick(function () {
              // * グラフの表示
              if (!this.isgraphDisplayed) {
                this.isgraphDisplayed = true;
                this.$refs.gfa.setGraphData(this.selectedMenu);
              }
            });
          } else {
            // 着果負担エリアがまだ未生成の場合
            if (!this.isFVDisplayed) {
              this.isFVDisplayed = true;
              this.$nextTick(function () {
                this.$refs.refFVActualValueInput.initialize(this.selectedMenu);
              });
            }
          }
        }
      }.bind(this)
    );
  },
  updated() {},
  components: {
    targetMenu,
    ph2GraphContainer,
    GEActualValueInput,
    ParmeterSetDialog,
    // GEFlValue,
    ReferenceFValue,
    LAActualValueInput,
    PEActualValueInput,
    FVActualValueInput,
  },
  created: function () {
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

    //************************************
    // ダイアログのオープン
    //************************************
    openDialog: function (item) {
      const selectedData = {
        title: this.selectedMenu.selectedModel.name,
        menu: this.selectedMenu,
        dates: this.dates,
      };
      this.sharedParam[item.type].setUp(
        this.$refs[item.key],
        function (dailog) {
          dailog.initialize(selectedData);
        }.bind(this),
        function () {
          this.setData();
        }.bind(this)
      );
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
