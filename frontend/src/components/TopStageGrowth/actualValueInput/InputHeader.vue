
<template>
    <v-container>
      <v-row no-gutters>
        <v-col :cols="3">
          <v-card-text>
            圃場名<br />
            <p class="font-weight-bold">{{ fieldName }}</p>
          </v-card-text>
        </v-col>
        <v-col>
          <v-card-text>
            デバイス名<br />
            <p class="font-weight-bold">{{ deviceName }}</p>
          </v-card-text>
        </v-col>
        <v-col>
          <v-card-text>
            年度<br />
            <p class="font-weight-bold">{{ year }}</p>
          </v-card-text>
        </v-col>
      </v-row>
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
    margin:0 5px;
    padding:0 5px;
  }
</style>
  