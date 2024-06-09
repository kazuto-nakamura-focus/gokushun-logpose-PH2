<template>
  <v-card elevation-6>
    <v-container>
      <v-row style="height: 20px">
        <v-col align="right">
          <div>
            <v-icon @click="handleClose()">mdi-close</v-icon>
          </div>
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
</template>
    
<script>
import Ph2GraphicTool from "@/components-v1/parts/graph/Ph2GraphicTool.vue";
import "@mdi/font/css/materialdesignicons.css";

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
      arguments: this.target,
      chart: { options: {}, series: [] },
      chartDisplay: false,
    };
  },
  //* ============================================
  // グラフの表示を行う下位関数をコールする
  //* ============================================
  mounted() {
    console.log("aaa");
    this.initialize();
  },
  methods: {
    //* ============================================
    // 単一グラフを作成する
    //* ============================================
    initialize() {
      this.chart.options = this.arguments.chartOption;
      this.chart.series = this.arguments.chartData;
      this.chartDisplay = true;
    },
    //* ============================================
    // 閉じる実行
    //* ============================================
    handleClose: function () {
      this.$emit("delete", this.arguments.id);
    },
  },
};
</script>
  