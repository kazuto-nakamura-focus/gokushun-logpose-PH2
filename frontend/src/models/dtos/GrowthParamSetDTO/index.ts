import HistoryDTO from "@/models/dtos/HistoryDTO";

type DTO = HistoryDTO & {
  id: number;
  bd: number;
  be: number;
  ad: number;
  ae: number;
};

export default DTO;
