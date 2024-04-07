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
const getAddress = function (lat, lon) {
    callAddressAPI(lat, lon).then((response) => {
        const address = response["address"];
        if (address["cuntry"] == "日本") {
            return address["province"] + address["city"] + address["quarter"] + address["road"]house_number;
        } else {
            return response["house_number"] + ", " + address["road"] + ", " + address["suburb"]
                + ", " + address["town"] + ", " + address["county"] + ", " + response["state"];
        }
    })
        .catch((error) => {
            console.log(error);
        });
}
export { getAddress };