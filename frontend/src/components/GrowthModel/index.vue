<template>
  <v-app>
    <v-container class="spacing-playground pa-5" fluid>
      <targetMenu ref="targetMenu" :shared="sharedMenu" :model="isModel"></targetMenu>
      <ph2GraphContainer ref="gfa" v-show="bodyStatus && selectedMenu.selectedModel.id != 4"></ph2GraphContainer>
      <div v-if="isFVDisplayed" v-show="bodyStatus && selectedMenu.selectedModel.id == 4">
        <FVActualValueInput ref="refFVActualValueInput" />
      </div>
    </v-container>
  </v-app>
</template>

<script>
import moment from "moment";
import targetMenu from "@/components/parts/Ph2TargetMenu.vue";

import ph2GraphContainer from "@/components-v1/parts/graph/ModelGraphContainer.vue";
import { DeviceParser } from "@/lib/deviceParser.js";
import { DialogController, MountController } from "@/lib/mountController.js";
import FVActualValueInput from "@/components/TopStageGrowth/actualValueInput/FVInput.vue";
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

          if (this.selectedMenu.selectedModel.id != 4) {
            // 着果負担エリアが表示中だった場合
            if (this.isFVDisplayed) {
              this.isFVDisplayed = false;
            } else {
              this.$nextTick(function () {
                // * グラフの表示
                this.$refs.gfa.setGraphData(this.selectedMenu);
              });
            }
          } else {
            // 着果負担エリアがまだ未生成の場合
            if (!this.isFVDisplayed) {
              this.isFVDisplayed = true;
            }
            this.$nextTick(function () {
              this.$refs.refFVActualValueInput.initialize(this.selectedMenu);
            });
          }
        }
      }.bind(this)
    );
  },
  updated() {},
  components: {
    targetMenu,
    ph2GraphContainer,
    // GEFlValue,
    FVActualValueInput,
  },
  created: function () {
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
