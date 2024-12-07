/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        customPink: '#F785B1',
        customBlue: '#9FC3E9',
        customPurple: '#9391E4',
        customRed: '#eb4343'
      }
    }
  },
  plugins: [
    require("daisyui"),
    // function ({ addUtilities }) {
    //   const newUtilities = {
    //     ".text-shadowCustom": {
    //       textShadow: "2px 2px 3px rgba(0, 0, 0, 0.2)"
    //     }
    //   }
    //   addUtilities(newUtilities)
    // }
  ],
  darkMode: 'false',
  daisyui: {
    darkTheme: false
  }
}
