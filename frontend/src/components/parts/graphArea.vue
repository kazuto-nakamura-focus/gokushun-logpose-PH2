<template>
  <div style="width: 100%; flex-wrap: wrap; display: flex">
    <div v-for="item in items" :key="item.id">
      <Graph ref="'gr'+item.id" :shared="shared[item.id]"></Graph>
    </div>
  </div>
</template>
<script>
import Graph from "@/components/parts/graph.vue";
import { MountController } from "@/lib/mountController.js";
export default {
  data() {
    return {
      items: [],
      shared: {},
      map: new Map(),
      index: 0,
    };
  },
  components: {
    Graph,
  },

  methods: {
    updateGraph: function (name, title, subTitle, data) {
      var index = this.map.get(name);
      if (index == undefined) {
        this.map.set(name, this.index);
        index = this.index++;
        this.items.push({ id: index });
        this.shared[index] = new MountController();
        this.shared[index].setUp(
          this.$refs['gr'+index],
          function (gr) {
            gr.initialize(title, subTitle, data);
          }.bind(this),
          function () {}.bind(this)
        );
      } else {
        this.$refs['gr'+index].initialize(title, subTitle, data);
      }
    },
  },
};
</script>