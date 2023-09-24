import Axios from "axios";
import { axiosEnvConfig } from "@/config/envConfig";

const axiosForMapAPI = Axios.create({
  baseURL: axiosEnvConfig.GSIUrl,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: false,
});

export default axiosForMapAPI;