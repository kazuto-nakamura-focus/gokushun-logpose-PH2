<template>
  <v-app>
    <v-card elevation-6>
      <v-container>
        <v-row>
          <v-col>
            <v-btn
              class="ml-1"
              v-for="(button, index) in editButtons"
              depressed
              color="primary"
              elevation="3"
              :key="index"
              @click="openDialog(button)"
            >{{ button.name }}</v-btn>
          </v-col>
          <v-col align="right">
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
              font-size: 10pt; font-family:Yu Gothic"
            >
              <b>{{ annotationLabel }}</b>
            </div>
          </v-col>
        </v-row>
        <v-row style="padding-bottom: 20px">
          <v-col align="left" style="font-size: 10pt; font-family: Yu Gothic;margin-left:6px">
            <b>{{ comment }}</b>
          </v-col>
        </v-row>
      </v-container>
      <!-- グラフ表示 -->
      <ph-2-graphic-tool
        v-if="chartDisplay == true"
        :options="chart.options"
        :series="chart.series"
      ></ph-2-graphic-tool>
    </v-card>
    <div v-if="modelId=1">
      <GEActualValueInput ref="refGEActualValueInput" :shared="sharedParam[0]" />
      <ReferenceFValue ref="refReferenceFValue" :shared="sharedParam[1]" />
      <parmeter-set-dialog ref="refGEParameterSets" :shared="sharedParam[2]" :modelId="modelId" />
    </div>

    <div v-if="modelId=2">
      <LAActualValueInput
        ref="refLAActualValueInput"
        :shared="sharedParam[0]"
        :selectedField="selectedMenu.selectedField"
        :selectedDevices="selectedMenu.selectedDevices"
      />
      <parmeter-set-dialog ref="refLAParameterSets" :shared="sharedParam[1]" :modelId="modelId" />
    </div>

    <div v-if="modelId=3">
      <PEActualValueInput
        ref="refPEActualValueInput"
        :shared="sharedParam[0]"
        :selectedField="selectedMenu.selectedField"
        :selectedDevices="selectedMenu.selectedDevices"
      />
      <parmeter-set-dialog ref="refPEParameterSets" :shared="sharedParam[1]" :modelId="photoModel" />
    </div>
  </v-app>
</template>
    
<script>
import Ph2GraphicTool from "@/components-v1/parts/graph/Ph2GraphicTool.vue";
import "@mdi/font/css/materialdesignicons.css";
import moment from "moment";
import allEditButtons from "@/components-v1/parts/graph/editButtons.json";
import { DialogController } from "@/lib/mountController.js";
import GEActualValueInput from "@/components-v1/GrowthModel/RealInput/GEMainInput.vue";
import ParmeterSetDialog from "@/components/TopStageGrowth/ParameterSet/ParmeterSetDialog.vue";
import ReferenceFValue from "@/components-v1/GrowthModel/RealFValueIput/index.vue";
import LAActualValueInput from "@/components-v1/LeafModel/RealValueInput/LAInput.vue";
import PEActualValueInput from "@/components/TopStageGrowth/actualValueInput/PEInput.vue";

export default {
  props: {
    target: {
      type: Object,
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
      console.log("ss");
      this.comment = this.target.data.comment;
      if (null != this.comment) {
        this.comment = "コメント:" + this.comment;
      }
      this.selectedMenu = this.target.selectedItems;
      this.modelId = this.selectedMenu.selectedModel.id;
      // * ボタンの表示
      this.editButtons =
        allEditButtons[this.target.selectedItems.selectedModel.id].buttons;
      //「実績」値の一覧
      const values = this.target.data?.values;
      //「推定」値の一覧
      const predictValues = this.target.data?.predictValues;
      // 「実測値」値の一覧
      const meauredValues = this.target.data?.meauredValues;
      //生育名毎の閾値
      const annotations = this.target.data?.annotations;

      //生育名の値を順番に比較するためのインデックス
      const tempLabels = [];
      if (annotations !== undefined && annotations !== null) {
        annotations.forEach((annotation) => {
          let date = "未達";
          if (annotation.date != null) {
            date = new moment(annotation.date).format("YYYY-MM-DD");
          }
          tempLabels.push(annotation.name + ":" + date);
        });
        if (tempLabels.length > 0) this.annotationLabel = tempLabels.join(", ");
      }
      this.chart.options = this.target.options;
      this.chart.series = [
        {
          name: "実績値",
          data: values,
        },
        {
          name: "推定値",
          data: predictValues,
        },
        {
          name: "実測値",
          data: meauredValues,
        },
      ];

      this.chartDisplay = true;
    },
    //* ============================================
    // ダイアログのオープン
    //* ============================================
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
        function (status) {
          if (status) this.$emit("doGraphAction", this.selectedMenu);
        }.bind(this)
      );
    },
    //* ============================================
    // 閉じる実行
    //* ============================================
    handleClose: function () {
      this.$emit("delete", this.target.id);
    },
  },
};
</script>
  