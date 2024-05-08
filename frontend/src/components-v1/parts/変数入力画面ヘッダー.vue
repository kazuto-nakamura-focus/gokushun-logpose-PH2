
<template>
  <v-container>
    <v-card-text>
      圃場名 :
      <b>{{ fieldName }}</b>
      <br />デバイス名 :
      <b>{{ deviceName }}</b>
      <br />年度 :
      <b>{{ year }}</b>
      <br />
    </v-card-text>

    <v-divider></v-divider>
  </v-container>
</template>
<script>
export default {
  data() {
    return {
      fieldName: "",
      deviceName: "",
      year: "",
    };
  },
  methods: {
    initialize: function (data) {
      this.fieldName = data.selectedField.name;
      this.deviceName = data.selectedDevice.name;
      this.year = data.selectedYear.id;
    },
  },
};
</script>
  <style lang="scss" scoped>
div {
  margin: 0 5px;
  padding: 0 5px;
}
</style>
  