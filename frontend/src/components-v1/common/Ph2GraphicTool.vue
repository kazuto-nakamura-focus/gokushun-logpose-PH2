<template>
  <div>
    <apexchart
      type="line"
      width="750"
      height="400"
      :options="options"
      :series="series"
    ></apexchart>
  </div>
</template>
<script>
import VueApexCharts from "vue-apexcharts";

export default {
  props: { options: { required: true }, series: { required: true } },
  components: { apexchart: VueApexCharts },
  /*data() {
    return {
      //* モデルID
      chartOptions: this.options,
      series: this.series,
    };
  },*/
};
</script>