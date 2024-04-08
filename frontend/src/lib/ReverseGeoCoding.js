import Axios from "axios";

const geoAxios = Axios.create({
    baseURL: "https://nominatim.openstreetmap.org/reverse",
});
const callAddressAPI = function (lat, lon) {
    let format = "json";
    let zoom = 18
    const config = {
        params: { lat, lon, format, zoom },
    };
    return geoAxios.get("", config);
}
export { callAddressAPI };