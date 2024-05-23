<template>
  <v-card width="100%">
    <v-container>
      <v-row>
        <div style="margin: 10px; color: black">
          <b>{{ title }}</b>
        </div>
      </v-row>
      <v-row>
        <div style="width: 100%; text-align: right; margin-left: 60px">
          <apexchart
            v-if="isCharts"
            type="bar"
            width="90%"
            :options="options"
            :series="series"
            ref="refChart"
          ></apexchart>
        </div>
      </v-row>
    </v-container>
  </v-card>
</template>
<script>
import VueApexCharts from "vue-apexcharts";

export default {
  data() {
    return {
      title: null,
      isCharts: false,
      options: {
        chart: {
          type: "bar",
        },
        plotOptions: {
          bar: {
            barhegiht: "30px",
          },
        },
        yaxis: {
          labels: {
            trim: false,
          },
        },
        xaxis: {
          labels: {
            trim: false,
          },
          categories: [""],
        },
        annotations: {
          yaxis: [
            {
              borderColor: "#00E396",
              borderWidth: 3,
            },
          ],
        },
      },
      series: [
        {
          data: [],
        },
      ],
    };
  },
  components: { apexchart: VueApexCharts },
  methods: {
    initialize(title, name, baseVaue, datalist) {
      console.log("aaa");
      this.options.xaxis.categories.length = 0;
      this.series[0].data.length = 0;

      this.options.annotations.yaxis[0].y = baseVaue;
      this.title = title;
      for (const item of datalist) {
        let parts = item.name.split("|");
        this.options.xaxis.categories.push([parts[0], parts[1], item.year]);
        this.series[0].data.push(item[name]);
      }
      this.isCharts = true;
      this.$nextTick(function () {
        this.$refs.refChart.updateOptions(this.options);
        this.$refs.refChart.updateSeries(this.series);
        this.$refs.refChart.refresh();
      });
    },
  },
};
</script>
<style lang="css" scoped>
.apexcharts-canvas .apexcharts2730mfpj .apexcharts-theme-light {
  width: 700px;
}
</style>