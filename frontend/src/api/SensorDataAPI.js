import axios from "@/lib/axiosHooks";
// センサーリストのデータ取得
const useSensoreList = (deviceId) => {
    const config = {
        params: {
            deviceId,
        },
    }
    return axios.get("/sensor/list", config);
};
//  センサーデータの取得
const useSensoreData = (deviceId, sensorId, startDate, endDate, interval) => {
    const config = {
        params: {
            deviceId,
            sensorId,
            startDate,
            endDate,
            interval
        },
    }
    return axios.get("/sensor/graph", config);
};
export {
    useSensoreList,
    useSensoreData,
};