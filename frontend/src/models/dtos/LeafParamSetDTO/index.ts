import HistoryDTO from "@/models/dtos/HistoryDTO";

type DTO = HistoryDTO & {
  id: number;
  areaA: number;
  areaB: number;
  areaC: number;
  countC: number;
  countD: number;
};

export default DTO;
