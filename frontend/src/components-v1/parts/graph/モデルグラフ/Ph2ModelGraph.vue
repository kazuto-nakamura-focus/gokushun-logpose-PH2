<template>
  <v-app>
    <v-card elevation-6>
      <v-container>
        <v-row>
          <v-col cols="11">
            <v-btn
              class="ml-1"
              v-for="(button, index) in editButtons"
              depressed
              color="primary"
              elevation="3"
              :key="index"
              @click="openDialog(button)"
              >{{ button.name }}</v-btn
            >
          </v-col>
          <v-col align="right" cols="1">
            <div>
              <v-icon @click="handleClose()">mdi-close</v-icon>
            </div>
          </v-col>
        </v-row>
        <v-row style="height: 36px">
          <v-col align="left" style="display: flex">
            <div
              v-if="annotationLabel"
              style="
                text-align: left;
                margin-left: 3px;
                padding: 5px;
                font-size: 10pt;
                font-family: Yu Gothic;
              "
            >
              <b>{{ annotationLabel }}</b>
            </div>
          </v-col>
        </v-row>
        <v-row style="padding-bottom: 20px">
          <v-col
            align="left"
            style="font-size: 10pt; font-family: Yu Gothic; margin-left: 6px"
          >
            <b>{{ comment }}</b>
          </v-col>
        </v-row>
      </v-container>
      <!-- グラフ表示 -->
      <ph-2-graphic-tool
        v-if="chartDisplay == true"
        ref="graphTool"
        :options="chart.options"
        :series="chart.series"
      ></ph-2-graphic-tool>
    </v-card>
    <div v-if="modelId == 1">
      <GEActualValueInput
        ref="refGEActualValueInput"
        :shared="sharedParam[0]"
      />
      <ReferenceFValue ref="refReferenceFValue" :shared="sharedParam[1]" />
      <parmeter-set-dialog
        ref="refGEParameterSets"
        :shared="sharedParam[2]"
        :modelId="modelId"
      />
    </div>

    <div v-if="modelId == 2">
      <LAActualValueInput
        ref="refLAActualValueInput"
        :shared="sharedParam[0]"
      />
      <parmeter-set-dialog
        ref="refLAParameterSets"
        :shared="sharedParam[1]"
        :modelId="modelId"
      />
    </div>

    <div v-if="modelId == 3">
      <PEActualValueInput
        ref="refPEActualValueInput"
        :shared="sharedParam[0]"
      />
      <parmeter-set-dialog
        ref="refPEParameterSets"
        :shared="sharedParam[1]"
        :modelId="modelId"
      />
    </div>
  </v-app>
</template>
    
<script>
import Ph2GraphicTool from "@/components-v1/parts/graph/Ph2GraphicTool.vue";
import "@mdi/font/css/materialdesignicons.css";
import allEditButtons from "@/components-v1/parts/graph/モデルグラフ/editButtons.json";
import { DialogController } from "@/lib/mountController.js";
import GEActualValueInput from "@/components-v1/GrowthModel/RealInput/GEMainInput.vue";
import ParmeterSetDialog from "@/components-v1/parts/パラメータセット編集/パラメータセット編集ダイアログ.vue";
import ReferenceFValue from "@/components-v1/GrowthModel/RealFValueIput/index.vue";
import LAActualValueInput from "@/components-v1/LeafModel/RealValueInput/葉面積葉枚数実績値入力画面.vue";
import PEActualValueInput from "@/components-v1/Photosynthesis/光合成量実績値入力.vue";

export default {
  props: {
    target: {
      type: Object, // GraphPanel
      required: true,
    },
  },
  components: {
    Ph2GraphicTool,
    GEActualValueInput,
    ParmeterSetDialog,
    ReferenceFValue,
    LAActualValueInput,
    PEActualValueInput,
  },

  data() {
    return {
      comment: "",
      chart: { options: {}, series: [] },
      chartDisplay: false,
      annotationLabel: null,
      editButtons: [],
      sharedParam: [
        new DialogController(),
        new DialogController(),
        new DialogController(),
      ],
      selectedMenu: null,
      modelId: 0,
    };
  },
  //* ============================================
  // グラフの表示を行う下位関数をコールする
  //* ============================================
  mounted() {
    this.initialize();
  },
  methods: {
    //* ============================================
    // 推定・実績グラフを作成する
    //* ============================================
    initialize() {
      this.comment = this.target.comment;
      this.selectedMenu = this.target.selectedItems;
      this.modelId = this.target.modelId;
      this.annotationLabel = this.target.annotationLabel;
      // * ボタンの表示
      this.editButtons = allEditButtons[this.modelId].buttons;

      this.chart.options = this.target.charOption;
      this.chart.series = this.target.chartData;
      this.chartDisplay = true;
    },
    //* ============================================
    // ダイアログのオープン
    //* ============================================
    openDialog: function (item) {
      const selectedData = {
        title: this.target.modelName,
        menu: this.selectedMenu,
      };
      this.sharedParam[item.type].setUp(
        this.$refs[item.key],
        function (dailog) {
          dailog.initialize(selectedData);
        }.bind(this),
        function (status) {
          if (status) this.$emit("doGraphAction", this.selectedMenu);
        }.bind(this)
      );
    },
    //* ============================================
    // タブが切り替わった時にX軸のラベルが消える問題の対応
    //  再表示を強制する
    //* ============================================
    /*setXLabel() {
      this.$refs.graphTool.setXLabel();
    },*/
    //* ============================================
    // 閉じる実行
    //* ============================================
    handleClose: function () {
      this.$emit("delete", this.target.id);
    },
  },
};
</script>
  