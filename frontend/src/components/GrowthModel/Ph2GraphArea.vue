<template>
  <div>
    <div id="graph-area"></div>
  </div>
</template>
<script>
import Vue from "vue/dist/vue.esm.js";
import Ph2GraphComponent from "../../components-v1/GrowthModel/graph/Ph2GraphComponent.vue";
import { MountController } from "@/lib/mountController.js";

export default {
  data() {
    return {
      //* モデルID
      modelId: 0,
      instances: [],
      graphList: [],
    };
  },

  methods: {
    addGraph: function (titlePath, chartOptions, chartData, isMultiple) {
      var shared = new MountController();
      var ComponentClass = Vue.extend(Ph2GraphComponent);
      var instance = new ComponentClass({
        propsData: {
          shared: shared,
        },
      });
      instance.$mount();
      document.getElementById("graph-area").prepend(instance.$el);
      this.instances.push(instance);
      shared.setUp(
        instance.$el,
        function (component) {
          if (isMultiple) {
            component.__vue__.initialize(titlePath, chartOptions, chartData);
          } else {
            component.__vue__.initializeSingle(
              titlePath,
              chartOptions,
              chartData
            );
          }
        }.bind(this),
        function (node) {
          node.$destroy();
          node.$el.parentNode.removeChild(node.$el);
        }.bind(this)
      );
    },
  },
};
</script>