import Label from "@/models/element/Label";

type DTO = Label & {
  field: string;
  brand: string;
  registeredDate: string;
  sigfoxId: string;
};

export default DTO;
