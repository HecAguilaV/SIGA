# SIGA - Commercial Portal

*Leer en otros idiomas: [![Español](README.md)](README.md)*

**Intelligent Asset Management System**
Commercial portal and subscription management for SaaS plans

**Live Demo:** [https://siga-webcomercial.vercel.app](https://siga-webcomercial.vercel.app)

---

## Project Philosophy

SIGA was born from a real problem: the operational paralysis that SMEs suffer.
Our mission is not just to record products, but to reduce operational friction by translating business intent into automatic actions.

### Pillars
1.  **Less Friction:** Intuitive interfaces that don't require manuals.
2.  **More Intent:** The system understands what you want to do (AI Assistant).
3.  **Automation:** Entrepreneurs should not stop for administrative tasks.

---

## Visual Identity and Experience

We have migrated from a generic interface to a premium proprietary identity:

*   **Theme:** **Deep Blue** (`#03045e`) with Cyan and Neon accents.
*   **Style:** **Glassmorphism** (Translucent glass panels).
*   **Real Dashboards:** We show the actual application interface from the first second.
*   **Unification:** The **AI Assistant** is now a visual twin of the main WebApp.

## Main Features

### Commercial Portal
- **Landing page** with information about SIGA and a demo video
- **Plan catalog** with prices in UF and CLP conversion
- **Persistent shopping cart** in localStorage
- **Authentication validation** before allowing payment
- **Simulated payment gateway** with professional design
- **Automatic invoice generation** after each purchase
- **Printable invoices** with professional design
- **Success page** with immediate invoice display
- **Complete purchase history** in the user profile
- **AI Assistant** for queries and data visualization (waste charts)

### User Management
- Integrated JWT authentication system
- Registration and login
- User profile with current plan information
- Personalized customer dashboard
- **Purchase and invoice history** accessible from profile
- **Individual invoice viewing and printing**

### Free Trial System
- **14-day free trial** for Pro and Growth plans
- One trial per user
- Automatic conversion to paid subscription
- Remaining days notifications

### Subscription Plans
1. **Emprendedor Pro** - 0.9 UF/month (Reference Prices)
   - SIGA AI Assistant
   - 2 warehouses/branches
   - 3 users
   - Advanced reports

2. **Crecimiento** - 1.9 UF/month (Reference Prices)
   - SIGA AI Assistant
   - Unlimited warehouses
   - Unlimited users
   - Accounting integrations
   - 24/7 priority support

### Admin Panel
- Portal metrics dashboard
- User CRUD management
- Plan CRUD management
- Active subscription view
- Password reset

### Economic Indicators Integration
- **mindicador.cl** API for UF and USD rates
- Automatic UF to CLP price conversion
- Chilean price formatting
- 5-minute cache for request optimization

### Invoice System
- **Automatic generation** after each purchase
- **Unique invoice numbers** (format: FAC-YYYYMMDD-XXXX)
- **Printable invoices** with paper-optimized professional design
- **Persistence** on backend with localStorage fallback
- **Full purchase history** accessible from profile
- **Invoice search** by number, ID, or user
- **Full API integration**
- Complete info: issuer, client, purchase details, payment method

### Security and Validation
- **Mandatory authentication validation** before allowing purchases
- **Route protection** for checkout and cart
- **Smart redirect** after login
- **Data validation** on payment forms

## Technologies Used

- **React 18.3** - Main framework
- **React Router DOM 6.26** - Navigation
- **Bootstrap 5.3** - Styles and UI components
- **Phosphor React 1.4** - Modern icon library
- **Recharts** - Data visualization chart library
- **Google Generative AI (@google/generative-ai)** - SDK for Gemini AI integration
- **Vite 5.4** - Build and development tool
- **Jasmine & Karma** - Unit testing
- **Babel & Webpack** - JSX processing for tests
- **pnpm 10.24.0** - Package manager (recommended for security)
- **Backend REST API** - Real API integration (Spring Boot)
- **localStorage** - Data persistence (fallback)
- **OpenAPI/Swagger** - API documentation (reference)

## Installation

### Prerequisites
- Node.js 16+ installed
- pnpm installed (recommended for better security)
- Git (optional)

### Install pnpm

If you don't have pnpm installed:
```bash
curl -fsSL https://get.pnpm.io/install.sh | sh -
```

**Important:** After installing, close and open a new terminal, or run:
```bash
source ~/.zshrc
```

If pnpm doesn't work after installation, run in your terminal:
```bash
export PNPM_HOME="$HOME/Library/pnpm"
export PATH="$PNPM_HOME:$PATH"
```

### Installation Steps

1. **Clone the repository**:
```bash
git clone https://github.com/HecAguilaV/SIGA-WEBCOMERCIAL.git
cd SIGA-WEBCOMERCIAL
```

2. **Install dependencies**:
```bash
pnpm install
```

3. **Configure environment variables** (optional, for AI assistant):
   - Create a `.env` file in the project root
   - ~~`VITE_GEMINI_API_KEY`~~ **NOT REQUIRED** - The AI assistant uses the backend endpoint which already has the API key configured

4. **Start development server**:
```bash
pnpm run dev
```

The application will be available at `http://localhost:5173`

## Usage

### Development

```bash
# Development server
pnpm run dev

# Production build
pnpm run build

# Production preview
pnpm run preview
```

### Testing

```bash
# Run unit tests
pnpm test
```

### Default Users

**Administrator:**
- Email: `admin@test.cl`
- Password: `test123`

**Operator / Customer:**
- Email: `oper@test.cl`
- Password: `test123`

## Project Structure

```
SIGA_WEB_COMERCIAL/
├── static/                 # Static files (logo, favicon, video, etc.)
│   ├── brand/             # SIGA logos and branding
│   ├── favicon/           # Icons and manifest
│   └── demo-sigaapp.mp4   # App demo video
├── src/
│   ├── components/        # Reusable components
│   │   ├── Boton.jsx
│   │   ├── CardPlan.jsx
│   │   ├── Navbar.jsx
│   │   ├── Footer.jsx
│   │   ├── AsistenteIA.jsx  # AI Assistant and chatbot
│   │   ├── GraficoTorta.jsx # Pie chart component
│   │   └── FacturaComponent.jsx  # Printable invoice component
│   ├── pages/            # Application pages
│   │   ├── HomePage.jsx  # Landing page with HTML5 video
│   │   ├── PlanesPage.jsx
│   │   ├── LoginPage.jsx
│   │   ├── RegistroPage.jsx
│   │   ├── CarritoPage.jsx
│   │   ├── CheckoutPage.jsx
│   │   ├── CompraExitosaPage.jsx
│   │   ├── PerfilPage.jsx
│   │   ├── AppPage.jsx
│   │   └── admin/        # Admin pages
│   ├── datos/            # Simulated data and CRUD
│   │   └── datosSimulados.js  # Includes waste data for charts
│   ├── utils/            # Utilities
│   │   ├── auth.js
│   │   ├── indicadoresEconomicos.js
│   │   └── contextoSIGA.js  # Full context for AI assistant
│   ├── styles/           # Global styles
│   │   └── index.css
│   ├── router.jsx        # Route configuration
│   ├── App.jsx          # Root component
│   └── main.jsx         # Entry point
├── tests/               # Unit tests
│   ├── boton.spec.jsx
│   ├── login.spec.jsx
│   ├── eliminarUsuario.spec.js
│   └── facturas.spec.js         # Invoice system tests
├── docs/                # Technical documentation
│   ├── ESTADO_TESTS.md           # Test status and plan
│   ├── api/
│   │   ├── openapi.yaml          # Swagger/OpenAPI documentation
│   │   └── README.md             # API documentation guide
│   └── ...
├── karma.conf.cjs       # Karma configuration (renamed for ES modules)
├── vite.config.js      # Vite configuration
├── package.json
├── README.md
├── GUIA_DE_ESTUDIO.md          # Full technical guide
├── COMENTARIOS_GUIA.md         # Guide for adding educational comments
├── RESUMEN_IMPLEMENTACION.md   # Implementation details
└── GUIA_GIT_RAMAS.md            # Git branch workflow guide
```

## Key Features

### Authentication System
- Real JWT authentication (Spring Boot Backend)
- Roles: `admin` (Administrator) and `operador` (Customer)
- Persistent sessions
- **Mandatory validation** before allowing purchases
- **Smart redirect** after login
- Sensitive route protection (checkout, cart)

### Subscription Management
- Plan assignment to users
- 14-day free trial
- Trial to paid subscription conversion
- Automatic revocation of expired trials

### Payment Gateway
- Professional real-gateway-like design
- Credit card validation
- Automatic card number formatting
- Card type detection (Visa/Mastercard)
- Processing simulation

### Economic Indicators
- Integration with mindicador.cl public API
- Current UF and USD values
- Automatic CLP conversion
- Chilean currency formatting

### Invoice System
- Automatic generation with unique numbers (FAC-YYYYMMDD-XXXX)
- Printable invoices with professional design
- Complete info: issuer, client, details, payment method
- localStorage persistence for full history
- Search by number, ID, or user
- Immediate display after purchase
- History accessible from user profile

### AI Assistant
- Intelligent chatbot with **Google Gemini AI** (gemini-2.5-flash model)
- Contextual responses based on full SIGA information
- Context includes: company info, plans, contact, location, services
- Waste charts visualization by category
- Integration with simulated business data
- Floating interface with quick access button (SIGA logo)
- Support for multiple message types (text and charts)
- Fallback to simulated responses if no API key configured
- Responsive and accessible design

### Landing Page Video
- Native HTML5 video without additional libraries
- Autoplay loop
- Web-optimized (MP4 with H.264 codec)
- Location: `/static/demo-sigaapp.mp4`
- Attributes: autoplay, loop, muted, playsInline

## Visual Identity

The application uses the official SIGA color palette:

- **Primary:** `#03045E` (Deep blue)
- **Accent:** `#00B4D8` (Light blue)
- **Secondary Accent:** `#80FFDB` (Turquoise)
- **White:** `#FFFFFF`

## Testing

The project includes unit tests with Jasmine and Karma:

- Component tests (`Boton`, `LoginPage`)
- CRUD function tests (`eliminarUsuario`, `facturas`)
- Coverage configuration
- Babel configured for JSX processing in tests

**Implemented tests:**
- Boton component
- LoginPage (email validation, successful login)
- Delete user
- Invoice system (create, get, search)

**Current coverage:** ~15% (target: 60-70%)

To run tests:
```bash
npm test
```

See full test documentation at [`docs/ESTADO_TESTS.md`](./docs/ESTADO_TESTS.md)

## Available Routes

### Public
- `/` - Landing page
- `/planes` - Plan catalog
- `/acerca` - About SIGA
- `/docs` - API documentation (Swagger UI)
- `/login` - Sign in
- `/registro` - User registration
- `/carrito` - Shopping cart
- `/checkout` - Payment gateway
- `/exito` - Purchase confirmation

### Protected (require authentication)
- `/perfil` - User profile
- `/app` - SIGA application (iframe)

### Admin (require admin role)
- `/admin` - Admin dashboard
- `/admin/usuarios` - User management
- `/admin/planes` - Plan management
- `/admin/suscripciones` - Active subscriptions

## Configuration

### Environment Variables

The project requires environment variables for advanced features:

#### For Local Development

1. Create a `.env` file in the project root
2. Add the following variables:

```env
# VITE_GEMINI_API_KEY - NOT REQUIRED
# The AI assistant uses the backend endpoint (/api/comercial/chat)
# The Gemini API key is configured in the backend (Railway)
```

**Get Gemini API Key:**
- Go to https://makersuite.google.com/app/apikey
- Create a new API key
- Copy the key and paste it in your `.env` file

**Note:** The `.env` file is in `.gitignore` and will not be uploaded to the repository.

#### For Vercel Deployment

**Required Environment Variables:**

1. Go to your project in **Vercel Dashboard**
2. Open **Settings** > **Environment Variables**
3. Add the following variables:

   **Variable 1: Backend URL (CRITICAL)**
   - **Name:** `VITE_API_BASE_URL`
   - **Value:** `https://siga-backend-production.up.railway.app`
   - **Environment:** Select all (Production, Preview, Development)

4. **Save** the variables
5. **Redeploy** the project for the variables to take effect

**Important:**
- The `VITE_` prefix is required for Vite to expose the variable to the client
- Without `VITE_API_BASE_URL`, the frontend will try to connect to `localhost:8080` and fail
- **NOTE:** The AI assistant uses the backend endpoint (`/api/comercial/chat`), does NOT require `VITE_GEMINI_API_KEY` in Vercel. The Gemini API key is configured in the backend (Railway).
- Never share your API keys publicly
- **After adding/modifying environment variables, you MUST redeploy**

### Server Port
The development server is configured to use port `5173` fixed. You can change it in `vite.config.js`:

```javascript
server: {
  port: 5173,
  host: true,
  strictPort: true,
}
```

### Static Files
Static files are served from the `static/` folder and accessible from the root (`/brand/Logo_SIGA.png`, `/demo-sigaapp.mp4`).

**Landing Page Video:**
- The video is loaded using the native HTML5 `<video>` element
- No additional libraries required
- Recommended format: MP4 with H.264 codec
- Location: `/static/demo-sigaapp.mp4`
- Attributes: `autoPlay`, `loop`, `muted`, `playsInline`

## Technical Documentation

All technical documents are located in the `docs/` folder:

1. **SRS (Software Requirements Specification)**
   - Location: `docs/ERS.md`
   - Complete system description, functional and non-functional requirements

2. **User Manual**
   - Location: `docs/MANUAL_USUARIO.md`
   - Complete step-by-step guide for end users

3. **Testing Coverage Document**
   - Location: `docs/COBERTURA_TESTING.md`
   - Current test status, coverage metrics, and improvement plan
   - HTML report available at `coverage/html/index.html` after running tests

4. **API Documentation**
   - OpenAPI specification: `docs/api/openapi.yaml`
   - Swagger UI accessible at `/docs` in the application
   - Additional documentation: `docs/api/README.md`

5. **APIs and Integration Document**
   - Location: `docs/APIS_INTEGRACION.md`
   - Integration architecture, endpoints, JWT authentication

---

## Data and Persistence

### Backend API (Primary)
The system is integrated with a REST API backend that handles:
- **Authentication:** Registration, login, refresh tokens
- **Plans:** Plan listing and details
- **Subscriptions:** Subscription creation and management
- **Invoices:** Invoice creation, listing, and search
- **Chat:** Commercial AI assistant

### localStorage Fallback
If the backend is unavailable, the system uses local data in `localStorage`:
- **Plans:** `siga_planes`
- **Users:** `siga_usuarios`
- **Subscriptions:** `siga_suscripciones`
- **Invoices:** `siga_facturas`
- **Authenticated user:** `siga_usuario_actual`
- **Cart:** `siga_carrito_plan`
- **Redirect:** `siga_redirect_after_login`
- **Current invoice:** `siga_factura_actual`

To clear test data (localStorage only), run in the browser console:
```javascript
localStorage.clear()
```

## Documentation and Testing

### Swagger/OpenAPI Documentation

View the complete API documentation interactively directly in the application:

**In-app Documentation:**
 [http://localhost:5173/docs](http://localhost:5173/docs) (local development)
 `https://your-domain.com/docs` (production)

**Swagger Editor (Online) - Alternative:**
 [View Swagger Documentation](https://editor.swagger.io/?url=https://raw.githubusercontent.com/HecAguilaV/SIGA-WEBCOMERCIAL/main/docs/api/openapi.yaml)

Or copy and paste this URL:
```
https://editor.swagger.io/?url=https://raw.githubusercontent.com/HecAguilaV/SIGA-WEBCOMERCIAL/main/docs/api/openapi.yaml
```

**31 documented endpoints** including:
- User management (7 endpoints)
- Plan management (6 endpoints)
- Subscription management (5 endpoints)
- Invoice system (5 endpoints)
- Authentication (3 endpoints)
- Cart (3 endpoints)
- Economic indicators (3 endpoints)

### Testing Interface (Jasmine/Karma)

To view the graphical test interface:

1. **Modify `karma.conf.cjs`** changing `ChromeHeadless` to `Chrome`:
```javascript
browsers: ['Chrome'], // Instead of ['ChromeHeadless']
singleRun: false,     // So it doesn't close automatically
```

2. **Run tests:**
```bash
pnpm test
```

3. **HTML Coverage Report:**
After running tests, open the generated HTML report:
```
coverage/html/index.html
```

The Karma interface will open in your browser showing all tests with real-time results.

## Future Features

- [ ] Real payment gateway integration
- [ ] Email notifications
- [ ] Dashboard with advanced charts
- [ ] Report export
- [ ] Real backend REST API
- [ ] PDF invoice generation
- [ ] Invoice email sending
- [ ] Advanced filters and search in purchase history
- [x] Google Gemini AI integration for the assistant
- [ ] More chart types in the assistant (bar, line, etc.)
- [ ] Chat with persistent history in the assistant

## License

This project is the foundation of SIGA.

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
