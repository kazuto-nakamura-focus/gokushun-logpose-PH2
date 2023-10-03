import { commonEnvConfig } from "@/config/envConfig";

const mapLoaderOptions = {
    apiKey: commonEnvConfig.googleMapApiKey,
    version: "weekly",
    // libraries: ["places", "drawing", "geometry", "visualization"],
    language: "ja",
}

const mapOptions = {
    zoom: 17,
    fullscreenControl: false,
    mapTypeControl: false,
    streetViewControl: true,
    // streetViewControlOptions: {
    //   position: google.maps.ControlPosition.LEFT_BOTTOM,
    // },
    zoomControl: true,
    // zoomControlOptions: {
    //   position: google.maps.ControlPosition.LEFT_BOTTOM,
    // },
    scaleControl: true,

    clickableIcons: true,
    draggable: true,
    // disableDoubleClickZoom: true,
    scrollwheel: true,
}

export {mapLoaderOptions, mapOptions}