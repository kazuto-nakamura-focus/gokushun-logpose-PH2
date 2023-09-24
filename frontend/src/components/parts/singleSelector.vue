<template>
  <div id="app">
    <multiselect
      v-model="selected"
      :options="sourceData"
      :searchable="true"
      label="title"
      :close-on-select="true"
      :multiple="false"
      :placeholder="title"
      :taggable="true"
      @select="select"
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
      selected: null,
      sourceData: [],
      title: "",
      prevSelect:null
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
      this.selected = null;
      for(const item in this.sourceData) {
        if(item.state) {
          this.selected = item;
        }
      }
    },

    select: function (item) {
      if(this.prevSelect != null) { this.prevSelect.state = false;}
      item.state = true;
      this.shared.onConclude(this.selected);
    },

  },
};
</script>
<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style src="@/style/selector.css" />
