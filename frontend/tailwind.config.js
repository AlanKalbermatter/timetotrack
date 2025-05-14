/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#3F3D56',     // dark violet/blue
        secondary: '#6C63FF',   // soft indigo
        accent: '#F9A826',      // amber
        background: '#F5F7FA',  // light gray
        text: '#2E2E2E'         // dark gray
      },
      fontFamily: {
        rounded: ['"Nunito"', 'sans-serif'],
      },
      transitionProperty: {
        width: 'width',
      },
    },
  },
  plugins: [],
  darkMode: 'class',

};