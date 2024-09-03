<template>
  <apexchart
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
};
</script>