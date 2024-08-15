/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        customPink: '#F785B1',
        customBlue: '#9FC3E9',
      },
    },
  },
  plugins: [require("daisyui")],
  darkMode: "false",
  daisyui: {
    darkTheme: false,
  },
}

