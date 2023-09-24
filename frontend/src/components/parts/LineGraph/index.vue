<template>
  <v-container>
    <v-row>
      <v-col>
        <div>
          <apexchart
            type="area"
            width="100%"
            height="400"
            ref="chart"
            :options="chart.options"
            :series="chart.series"
          ></apexchart>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import VueApexCharts from "vue-apexcharts";
export default {
  props: {
    chartData: {
      type: Object,
      required: true,
    },
    chartOptions: {
      type: Object,
      required: false,
    },
    chartStyle: {
      type: Object,
      required: false,
    },
    tickHeight: {
      type: Number,
      required: false,
    },
  },
  components: {
    apexchart: VueApexCharts,
  },
  data() {
    return {
      chart: {
        options: {},
        series: [
          {
            name: "",
            data: [], // Y軸の値
          },
          {
            name: "",
            data: [], // Y軸の値
          },
        ],
      },
    }
  },
  mounted() {

  },
  methods: {
    updateData(options, data) {
      
     // let startDate = data.xstart;
      this.chart.series[0].name = "実績";
      this.chart.series[0].data.length=0;
      this.chart.series[1].name = "推定";
      this.chart.series[1].data.length=0;
      for (const item of data.values) {
        this.chart.series[0].data.push(item);
        this.chart.series[1].data.push(item);
      }

      for (const item of data.predictValues) {
        this.chart.series[0].data.push(null);
        this.chart.series[1].data.push(item);
      }
      this.$refs.chart.updateSeries(
        [
          {
            data: this.chart.series[0].data,
          },
          {
            data: this.chart.series[1].data,
          },
        ],
        false,
        true
      );
      this.chart.options = options;
      this.$refs.chart.updateOptions(this.chart.options, false, true, false);
      this.$refs.chart.refresh();
    },
  },
};
</script>
