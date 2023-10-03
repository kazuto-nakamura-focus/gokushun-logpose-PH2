<template>
  <v-app>
    <v-card elevation="3" width="80%">
      <div class="cm_Title">
        {{ title }}
      </div>
      <v-data-table
        :headers="headers"
        :items="selectedDataList"
        mobile-breakpoint="0"
        :sort-by.sync="sortBy"
        :sort-desc.sync="sortDesc"
      >
      </v-data-table>
    </v-card>
  </v-app>
</template>
<script>
import { useSum } from "@/api/Top";
export default {
  props: { shared /** MountController */: { required: true } },
  data() {
    return {
      title: "",
      selectedDataList: [],
      sortBy: "value",
      sortDesc: false,
      headers: [
        { text: "圃場", value: "name", sortable: true, width: "30%" },
        { text: "センサー", value: "sensorName", sortable: true, width: "30%" },
        { text: "値", value: "value", sortable: true, width: "10%" },
        { text: "測定日", value: "date", sortable: true, width: "30%" },
      ],
    };
  },

  mounted() {
    this.shared.mount(this);
  },

  methods: {
    initialize: function (koumoku) {
      this.selectedDataList = [];
      this.title = koumoku.name;
      useSum(koumoku.variable)
        .then((response) => {
          //成功時
          const results = response["data"];
          for (const item of results.data) {
            var n = 2 ;	// 小数点第n位まで残す
            item.value = Math.floor( item.value * Math.pow( 10, n ) ) / Math.pow( 10, n ) ;
            var selected = {
              name: item.name,
              sensorName:item.sensorName,
              value: item.value,
              date:item.castedAt.substring(0,19)
            };
            this.selectedDataList.push(selected);
          }
        })
        .catch((error) => {
          //失敗時
          console.log(error);
        });
    },
  },
  created: function () {
    this.sourceData = this.$store.getters.sourceData;
  },
};
</script>
<style scoped>
</style>