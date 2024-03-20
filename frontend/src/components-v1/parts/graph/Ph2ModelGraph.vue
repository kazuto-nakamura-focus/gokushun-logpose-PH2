<template>
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
    <ph-2-graphic-tool v-if="chartDisplay == true" :options="chart.options" :series="chart.series"></ph-2-graphic-tool>
  </v-card>
</template>
    
<script>
import Ph2GraphicTool from "@/components-v1/parts/graph/Ph2GraphicTool.vue";
import "@mdi/font/css/materialdesignicons.css";
import moment from "moment";
import allEditButtons from "@/components-v1/parts/graph/editButtons.json";

export default {
  props: {
    target: {
      type: Object,
      required: true,
    },
  },
  components: {
    ph2GraphicTool: Ph2GraphicTool,
  },

  data() {
    return {
      comment: "",
      chart: { options: {}, series: [] },
      chartDisplay: false,
      annotationLabel: null,
      // selectedMenu: this.target.selectedMenu,
      //  modelId: this.selectedMenu.selectedModel.id,
      editButtons: [],
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
    // 閉じる実行
    //* ============================================
    handleClose: function () {
      this.$emit("delete", this.target.id);
    },
  },
};
</script>
  