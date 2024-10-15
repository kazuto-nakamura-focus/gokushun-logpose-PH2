<template>
  <apexchart
    ref="chart"
    type="line"
    width="90%"
    height="550"
    :options="options"
    :series="series"
  ></apexchart>
</template>
<script>
import VueApexCharts from "vue-apexcharts";

export default {
  props: { options: { required: true }, series: { required: true } },
  components: { apexchart: VueApexCharts },

  methods: {
    //* ============================================
    // タブが切り替わった時にX軸のラベルが消える問題の対応
    //  再表示を強制する
    //* ============================================
    setXLabel: function () {
      this.$nextTick(() => {
        const chart = this.$refs.chart.chart; // Vue2では直接chartにアクセス
        chart.zoomX(
          chart.w.globals.minX, // 最小X値
          chart.w.globals.maxX // 最大X値
        );
      });
    },
  },
};
</script>