<template>
  <div class="text-center">
    <v-select
      v-model="sensor"
      :items="sensors"
      item-text="name"
      item-value="id"
      label="センサー名"
    ></v-select>
  </div>
</template>
  
<script>
export default {
  data: () => ({
    sensor: null,
    sensors: [],
  }),
  methods: {
    setMenu: function (sensorList) {
      this.sensors = sensorList;
    },
    setDefault: function (sensorId) {
      this.sensor = sensorId;
    },
    getSelected: function () {
      return this.sensor;
    },
  },
};
</script>