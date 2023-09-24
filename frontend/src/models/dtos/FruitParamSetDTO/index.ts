import HistoryDTO from "@/models/dtos/HistoryDTO";

type DTO = HistoryDTO & {
  id: number;
  fieldF: number;
  fieldG: number;
  weibullA: number;
  weibullB: number;
  weibullL: number;
};

export default DTO;
