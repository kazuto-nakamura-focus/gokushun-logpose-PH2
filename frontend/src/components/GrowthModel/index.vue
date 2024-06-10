<template>
  <v-app>
    <v-container class="spacing-playground pa-5" fluid>
      <targetMenu
        ref="targetMenu"
        :shared="sharedMenu"
        :model="isModel"
      ></targetMenu>
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
  </v-app>
</template>

<script>
import targetMenu from "@/components/parts/Ph2TargetMenu.vue";

import ph2GraphContainer from "@/components-v1/parts/graph/モデルグラフ/ModelGraphContainer.vue";
import { MountController } from "@/lib/mountController.js";
import FVActualValueInput from "@/components-v1/FruitBearing/着果量着果負担トップ.vue";

export default {
  data() {
    return {
      // 定数
      isModel: true,
      bodyStatus: false,
      sourceData: [],
      selectedMenu: null,
      selectedField: [],
      sharedMenu: new MountController(),
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
  components: {
    targetMenu,
    ph2GraphContainer,
    FVActualValueInput,
  },
  methods: {
    setSelectedList: function () {
      this.selectedField = [];
      for (const item of this.sourceData) {
        if (item.state) {
          this.selectedField.push(item);
        }
      }
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
