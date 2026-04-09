import type { ChangeEvent, InputHTMLAttributes } from 'react';
import MuiTextField from '@mui/material/TextField';

type TextFieldProps = {
  label: string;
  value: string | number;
  onChange: (event: ChangeEvent<HTMLInputElement>) => void;
  helpText?: string;
  error?: string;
  required?: boolean;
  disabled?: boolean;
  type?: string;
  inputProps?: InputHTMLAttributes<HTMLInputElement>;
  multiline?: boolean;
  minRows?: number;
};

const TextField = ({
  label,
  value,
  onChange,
  helpText,
  error,
  required,
  disabled,
  type,
  inputProps,
  multiline,
  minRows,
}: TextFieldProps) => {
  return (
    <MuiTextField
      label={label}
      value={value}
      onChange={onChange}
      helperText={error || helpText}
      error={Boolean(error)}
      required={required}
      disabled={disabled}
      type={type}
      size="small"
      fullWidth
      inputProps={inputProps}
      multiline={multiline}
      minRows={minRows}
    />
  );
};

export default TextField;
