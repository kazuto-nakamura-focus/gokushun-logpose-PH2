<template>
  <div id="app">
    <multiselect
      v-model="selected"
      :options="sourceData"
      :searchable="true"
      label="title"
      :close-on-select="true"
      :multiple="true"
      :placeholder="title"
      :taggable="true"
      track-by="title"
      @select="select"
      @remove="deselect"
    >
    </multiselect>
  </div>
</template>
  
  <script>
import multiselect from "vue-multiselect";

export default {
  props: { shared /** MountController */: { required: true } },

  data() {
    return {
      selected: [],
      sourceData: [],
      title: "",
    };
  },

  components: {
    multiselect,
  },

  mounted() {
    this.shared.mount(this);
  },

  methods: {
    initialize: function (title, sourceData) {
      this.title = title;
      this.sourceData = sourceData;
      this.selected = [];
    },

    select: function (item) {
      item.state = true;
      this.shared.onConclude(this.selected);
    },

    deselect: function (item) {
      item.state = false;
      this.shared.onConclude(this.selected);
    },
  },
};
</script>
<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style src="@/style/selector.css" scoped>