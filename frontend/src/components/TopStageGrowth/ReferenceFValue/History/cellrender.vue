<template>
  <button @click="buttonClicked()">Push For Total</button>
</template>

<script>
import Vue from "vue";

export default Vue.extend({
  data: function () {
    return {
      cellValue: null,
    };
  },
  beforeMount() {
    // this.params contains the cell and row information and is made available to this component at creation time
    // see ICellRendererParams for more details
    this.cellValue = this.getValueToDisplay(this.params);
  },
  methods: {
    buttonClicked() {
      alert(`${this.cellValue} medals won!`);
    },

    // getValueToDisplay(params) {
    //   return params.valueFormatted ? params.valueFormatted : params.value;
    // },
  },
});
</script>