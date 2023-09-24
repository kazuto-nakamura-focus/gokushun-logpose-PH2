<template>
  <v-app>
    <v-container>
      <v-row>
        <v-col cols="4">
          <v-text-field
            dense
            hide-details="auto"
            outlined
            v-model="useFieldInfoAddData.name"
            label="圃場名"
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6">
          <v-sheet
            id="map"
            class="pa-0"
            :light="true"
            style="width: 550px; height: 400px"
          />
          <p></p>
          <v-row cols="3" justify="center">
            <v-col cols="3">
              <v-subheader>GPS Code</v-subheader>
            </v-col>
            <v-col cols="9">
              <!-- <v-text-field dense hide-details="auto" outlined filled readonly></v-text-field> -->
              <v-text-field
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                v-model="inputValue"
              ></v-text-field>
            </v-col>
          </v-row>
        </v-col>

        <v-col cols="6">
          <v-row>
            <v-col cols="3">
              <v-subheader>場所</v-subheader>
            </v-col>
            <v-col cols="6">
              <v-text-field
                dense
                hide-details="auto"
                outlined
                filled
                readonly
                v-model="useFieldInfoAddData.location"
              ></v-text-field>
            </v-col>
          </v-row>
          <p></p>
          <v-row>
            <v-col cols="3">
              <v-subheader>取引先</v-subheader>
            </v-col>
            <v-col cols="6">
              <v-text-field
                dense
                hide-details="auto"
                outlined
                v-model="useFieldInfoAddData.contructor"
              ></v-text-field>
            </v-col>
          </v-row>
          <p></p>
          <v-row>
            <v-col cols="3">
              <v-subheader>登録日時</v-subheader>
            </v-col>
            <v-col cols="6">
              <v-text-field
                dense
                hide-details="auto"
                outlined
                readonly
                filled
                v-model="useFieldInfoAddData.registeredDate"
              ></v-text-field>
            </v-col>
          </v-row>
          <p></p>
        </v-col>
      </v-row>
      <v-card-actions>
        <v-spacer></v-spacer>
        <div class="GS_ButtonArea">
          <v-btn
            color="primary"
            class="ma-2 white--text"
            elevation="2"
            @click="add()"
            >保存</v-btn
          >
          <v-btn
            color="gray"
            class="ma-2 black--text"
            elevation="2"
            @click="back()"
            >キャンセル</v-btn
          >
        </div>
      </v-card-actions>
    </v-container>
  </v-app>
</template>

<script>
import { Loader } from "@googlemaps/js-api-loader";
import { useFieldInfoAdd } from "@/api/ManagementScreenTop/MSField";
import moment from "moment";

import { commonEnvConfig } from "@/config/envConfig";

export default {
  props: {
    onAdd: {
      type: Function,
      required: false,
    },
    onBackPressed: {
      type: Function,
      required: false,
    },
  },

  data() {
    return {
      useFieldInfoAddData: {
        name: "",
        location: "",
        latitude: "",
        longitude: "",
        contructor: "",
        registeredDate: moment().format("YYYY-MM-DD"),
      },
      inputValue: "",
      splitArr: [],
      marker: null,
      address: "",
    };
  },

  watch: {
    inputValue(newVal) {
      this.splitArr = newVal.split(",");
    },
  },

  mounted() {
    new Loader({
      apiKey: commonEnvConfig.googleMapApiKey,
      version: "frozen",
      libraries: ["places", "drawing", "geometry", "visualization"],
      language: "ja",
    })
      .load()
      .then((google) => {
        this.google = google;
        // 地図の初期化
        this.map = new google.maps.Map(document.getElementById("map"), {
          // 初期表示設定
          zoom: 17,
          center: { lat: 35.692195, lng: 139.759854 }, // マルティスープ本社
          fullscreenControl: false,
          mapTypeControl: false,
          streetViewControl: true,
          streetViewControlOptions: {
            position: google.maps.ControlPosition.LEFT_BOTTOM,
          },
          zoomControl: true,
          zoomControlOptions: {
            position: google.maps.ControlPosition.LEFT_BOTTOM,
          },
          scaleControl: true,
        });

        // ↓
        // こちらにレスポンスとして受け取ったgoogleやthis.mapを使用すれば、
        this.map.addListener("click", this.handleMapClick);
        // 通常通りvueでもJavaScriptAPIを利用できます。
        // ↑
      })
      .catch((e) => {
        console.error(e);
      });
  },

  methods: {
    handleMapClick(event) {
      if (this.marker) {
        this.marker.setMap(null);
      }
      const latLng = event.latLng;
      const latitude = latLng.lat();
      const longitude = latLng.lng();
      this.marker = new this.google.maps.Marker({
        position: { lat: latitude, lng: longitude },
        map: this.map,
      });
      this.getAddressFromLatLng({ lat: latitude, lng: longitude });
      this.inputValue = latitude + "," + longitude;
    },

    getAddressFromLatLng(latLng) {
      const geocoder = new this.google.maps.Geocoder();
      geocoder.geocode({ location: latLng }, (results, status) => {
        if (status === "OK") {
          if (results[0]) {
            this.address = this.parseAddress(results[0].address_components);
            console.log("getAddressFromLatLng_address", this.address);
            this.useFieldInfoAddData.location =
              this.address.city +
              ", " +
              this.address.prefecture +
              ", " +
              this.address.country;
          } else {
            console.log("No results found");
          }
        } else {
          console.log("Geocoder failed due to: " + status);
        }
      });
    },

    parseAddress(addressComponents) {
      let city = "";
      let prefecture = "";
      let country = "";
      for (let component of addressComponents) {
        if (component.types.includes("locality")) {
          city = component.long_name;
        }
        if (component.types.includes("administrative_area_level_1")) {
          prefecture = component.long_name;
        }
        if (component.types.includes("country")) {
          country = component.long_name;
        }
      }
      let parseAddressData = {
        city,
        prefecture,
        country,
      };
      return parseAddressData;
    },

    add: function () {
      if (confirm("登録してもよろしいですか？")) {
        console.log("splitArr", this.splitArr);
        console.log("latitude", this.splitArr[0]);
        console.log("longitude", this.splitArr[1]);

        const data = {
          id: null,
          name: this.useFieldInfoAddData.name,
          location: this.useFieldInfoAddData.location,
          latitude: parseFloat(this.splitArr[0]),
          longitude: parseFloat(this.splitArr[1]),
          contructor: this.useFieldInfoAddData.contructor,
          // registeredDate: null
          registeredDate: moment().format("YYYY-MM-DD"),
        };

        console.log("add_data", data);

        //圃場情報追加(API)
        useFieldInfoAdd(data)
          .then((response) => {
            //成功時
            const { status, message } = response["data"];
            if (status === 0) {
              alert("登録を成功しました。");
              this.$emit("refreshFieldList");
              this.onAdd();
            } else {
              throw new Error(message);
            }
          })
          .catch((error) => {
            //失敗時
            console.log(error);
            alert("登録を失敗しました。");
          });
      }
    },
    back: function () {
      this.onBackPressed();
    },
  },
};
</script>
