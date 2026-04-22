-- ============================================================
-- SIGA - PostgreSQL Database (Global Standard)
-- Relational Model: 5 Schemas + PGVector
-- Version: 2.2 (English)
-- ============================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "vector";

-- ============================================================
-- SCHEMA: auth (Identity & Access Management)
-- Service: auth (:8081)
-- ============================================================

CREATE SCHEMA auth;
COMMENT ON SCHEMA auth IS 'Authentication, operational users, permissions and store assignments';

-- -------------------------------------------------------
-- Table: auth.users
-- Operational users (company employees)
-- -------------------------------------------------------
CREATE TABLE auth.users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMINISTRATOR', 'OPERATOR', 'CASHIER')),
    commercial_user_id INTEGER,  -- Logical reference to tenant (commercial.customers)
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_email ON auth.users(email);
CREATE INDEX idx_users_commercial ON auth.users(commercial_user_id);

-- -------------------------------------------------------
-- Table: auth.permissions
-- Catalog of allowed operations
-- -------------------------------------------------------
CREATE TABLE auth.permissions (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_permissions_code ON auth.permissions(code);
CREATE INDEX idx_permissions_category ON auth.permissions(category);

-- -------------------------------------------------------
-- Table: auth.role_permissions
-- Template of permissions per role
-- -------------------------------------------------------
CREATE TABLE auth.role_permissions (
    role VARCHAR(20) NOT NULL,
    permission_id INTEGER NOT NULL,
    PRIMARY KEY (role, permission_id),
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) 
        REFERENCES auth.permissions(id) ON DELETE CASCADE
);
CREATE INDEX idx_role_permissions_permission ON auth.role_permissions(permission_id);

-- -------------------------------------------------------
-- Table: auth.user_permissions
-- Additional permissions per user
-- -------------------------------------------------------
CREATE TABLE auth.user_permissions (
    user_id INTEGER NOT NULL,
    permission_id INTEGER NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by INTEGER,
    PRIMARY KEY (user_id, permission_id),
    CONSTRAINT fk_user_perm_user FOREIGN KEY (user_id) 
        REFERENCES auth.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_perm_permission FOREIGN KEY (permission_id) 
        REFERENCES auth.permissions(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Table: auth.user_stores
-- M:N assignment of users to stores
-- -------------------------------------------------------
CREATE TABLE auth.user_stores (
    user_id INTEGER NOT NULL,
    store_id INTEGER NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, store_id),
    CONSTRAINT fk_user_store_user FOREIGN KEY (user_id) 
        REFERENCES auth.users(id) ON DELETE CASCADE
);

-- ============================================================
-- SCHEMA: inventory (Inventory Management)
-- Service: inventory (:8082)
-- ============================================================

CREATE SCHEMA inventory;
COMMENT ON SCHEMA inventory IS 'Products, categories, stock, movements and alerts';

-- -------------------------------------------------------
-- Table: inventory.stores
-- Company branches or warehouses
-- -------------------------------------------------------
CREATE TABLE inventory.stores (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    city VARCHAR(100),
    commercial_user_id INTEGER NOT NULL,  -- Tenant reference
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_stores_commercial ON inventory.stores(commercial_user_id);

-- -------------------------------------------------------
-- Table: inventory.categories
-- Product grouping
-- -------------------------------------------------------
CREATE TABLE inventory.categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    commercial_user_id INTEGER NOT NULL,  -- Tenant reference
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_categories_name_commercial UNIQUE (name, commercial_user_id)
);
CREATE INDEX idx_categories_commercial ON inventory.categories(commercial_user_id);

-- -------------------------------------------------------
-- Table: inventory.products
-- Master product catalog
-- -------------------------------------------------------
CREATE TABLE inventory.products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id INTEGER,
    barcode VARCHAR(50) UNIQUE,
    unit_price DECIMAL(10,2),
    commercial_user_id INTEGER NOT NULL,  -- Tenant reference
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) 
        REFERENCES inventory.categories(id) ON DELETE SET NULL
);
CREATE INDEX idx_products_commercial ON inventory.products(commercial_user_id);
CREATE INDEX idx_products_barcode ON inventory.products(barcode);

-- -------------------------------------------------------
-- Table: inventory.stock
-- Current quantity per product/store
-- -------------------------------------------------------
CREATE TABLE inventory.stock (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    store_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    minimum_quantity INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_stock_product_store UNIQUE (product_id, store_id),
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) 
        REFERENCES inventory.products(id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_store FOREIGN KEY (store_id) 
        REFERENCES inventory.stores(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Table: inventory.movements
-- Kardex: full history of stock changes
-- -------------------------------------------------------
CREATE TABLE inventory.movements (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    store_id INTEGER NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('IN', 'OUT', 'SALE', 'ADJUSTMENT', 'TRANSFER')),
    previous_quantity INTEGER NOT NULL,
    new_quantity INTEGER NOT NULL,
    quantity INTEGER NOT NULL,  -- difference
    user_id INTEGER,  -- Logical reference to auth.users
    sale_id INTEGER,  -- Logical reference to sales.sales
    observations TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movement_product FOREIGN KEY (product_id) 
        REFERENCES inventory.products(id) ON DELETE CASCADE,
    CONSTRAINT fk_movement_store FOREIGN KEY (store_id) 
        REFERENCES inventory.stores(id) ON DELETE CASCADE
);
CREATE INDEX idx_movements_product ON inventory.movements(product_id);
CREATE INDEX idx_movements_created_at ON inventory.movements(created_at);

-- -------------------------------------------------------
-- Table: inventory.alerts
-- Automatic inventory notifications
-- -------------------------------------------------------
CREATE TABLE inventory.alerts (
    id SERIAL PRIMARY KEY,
    type VARCHAR(30) NOT NULL CHECK (type IN ('LOW_STOCK', 'OUT_OF_STOCK', 'HIGH_SALES', 'SUSPICIOUS_MOVEMENT')),
    product_id INTEGER,
    store_id INTEGER,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alert_product FOREIGN KEY (product_id) 
        REFERENCES inventory.products(id) ON DELETE CASCADE,
    CONSTRAINT fk_alert_store FOREIGN KEY (store_id) 
        REFERENCES inventory.stores(id) ON DELETE CASCADE
);

-- ============================================================
-- SCHEMA: sales (Point of Sale - POS)
-- Service: sales (:8083)
-- ============================================================

CREATE SCHEMA sales;
COMMENT ON SCHEMA sales IS 'POS, sales, cash shifts, transactions and cart';

-- -------------------------------------------------------
-- Table: sales.payment_methods
-- Payment method catalog
-- -------------------------------------------------------
CREATE TABLE sales.payment_methods (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT true
);

INSERT INTO sales.payment_methods (name) VALUES 
    ('CASH'), ('DEBIT_CARD'), ('CREDIT_CARD'), ('TRANSFER');

-- -------------------------------------------------------
-- Table: sales.cash_shifts
-- Opening and closing of cash register per cashier/store
-- -------------------------------------------------------
CREATE TABLE sales.cash_shifts (
    id SERIAL PRIMARY KEY,
    store_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,  -- Logical reference to auth.users
    opened_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    initial_amount DECIMAL(10,2) NOT NULL,
    final_amount DECIMAL(10,2),
    counted_amount DECIMAL(10,2),  -- Physical count for reconciliation
    status VARCHAR(10) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'CLOSED'))
);

-- -------------------------------------------------------
-- Table: sales.sales
-- Sales record
-- -------------------------------------------------------
CREATE TABLE sales.sales (
    id SERIAL PRIMARY KEY,
    store_id INTEGER NOT NULL,
    user_id INTEGER,  -- Logical reference to auth.users
    total DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED' 
        CHECK (status IN ('COMPLETED', 'CANCELLED', 'PENDING')),
    observations TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_sales_created_at ON sales.sales(created_at);

-- -------------------------------------------------------
-- Table: sales.sale_items
-- Product lines per sale
-- -------------------------------------------------------
CREATE TABLE sales.sale_items (
    id SERIAL PRIMARY KEY,
    sale_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,  -- Logical reference to inventory.products
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) 
        REFERENCES sales.sales(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Table: sales.pos_transactions
-- Payment details per transaction
-- -------------------------------------------------------
CREATE TABLE sales.pos_transactions (
    id SERIAL PRIMARY KEY,
    sale_id INTEGER NOT NULL,
    shift_id INTEGER NOT NULL,
    payment_method_id INTEGER NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    last_4_digits VARCHAR(4),
    payment_reference VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_sale FOREIGN KEY (sale_id) 
        REFERENCES sales.sales(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_shift FOREIGN KEY (shift_id) 
        REFERENCES sales.cash_shifts(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_payment_method FOREIGN KEY (payment_method_id) 
        REFERENCES sales.payment_methods(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Table: sales.pos_cart
-- Temporary cart during sale
-- -------------------------------------------------------
CREATE TABLE sales.pos_cart (
    id SERIAL PRIMARY KEY,
    sale_id INTEGER,  -- Nullable until completed
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SCHEMA: commercial (SaaS Portal & Billing)
-- Service: commercial (:8084)
-- ============================================================

CREATE SCHEMA commercial;
COMMENT ON SCHEMA commercial IS 'SaaS portal: customers, plans, subscriptions, payments and billing';

-- -------------------------------------------------------
-- Table: commercial.customers
-- Portal clients (business owners)
-- -------------------------------------------------------
CREATE TABLE commercial.customers (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    tax_id VARCHAR(20),  -- RUT in Chile
    phone VARCHAR(20),
    company_name VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'customer' CHECK (role IN ('admin', 'customer')),
    is_on_trial BOOLEAN NOT NULL DEFAULT true,
    trial_start_at TIMESTAMP,
    trial_end_at TIMESTAMP,
    plan_id INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_commercial_customers_email ON commercial.customers(email);

-- -------------------------------------------------------
-- Table: commercial.plans
-- SaaS plans catalog
-- -------------------------------------------------------
CREATE TABLE commercial.plans (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    store_limit INTEGER NOT NULL DEFAULT 1,
    user_limit INTEGER NOT NULL DEFAULT 3,
    product_limit INTEGER,  -- NULL = unlimited
    monthly_price DECIMAL(10,2) NOT NULL,
    annual_price DECIMAL(10,2),
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true
);

INSERT INTO commercial.plans (name, description, store_limit, user_limit, product_limit, monthly_price, annual_price, sort_order) VALUES
    ('Basic', 'For small businesses', 1, 3, 50, 29000, 290000, 1),
    ('Professional', 'For growing businesses', 3, 10, 200, 59000, 590000, 2),
    ('Enterprise', 'For medium enterprises', 10, 50, NULL, 149000, 1490000, 3);

-- -------------------------------------------------------
-- Table: commercial.subscriptions
-- Active contracts
-- -------------------------------------------------------
CREATE TABLE commercial.subscriptions (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    plan_id INTEGER NOT NULL,
    period VARCHAR(20) NOT NULL DEFAULT 'MONTHLY' CHECK (period IN ('MONTHLY', 'ANNUAL')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' 
        CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CANCELLED', 'EXPIRED')),
    start_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_at TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subscription_customer FOREIGN KEY (customer_id) 
        REFERENCES commercial.customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_subscription_plan FOREIGN KEY (plan_id) 
        REFERENCES commercial.plans(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Table: commercial.payments
-- Payment records
-- -------------------------------------------------------
CREATE TABLE commercial.payments (
    id SERIAL PRIMARY KEY,
    subscription_id INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' 
        CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    reference VARCHAR(100),
    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_subscription FOREIGN KEY (subscription_id) 
        REFERENCES commercial.subscriptions(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_customer FOREIGN KEY (customer_id) 
        REFERENCES commercial.customers(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Table: commercial.invoices
-- Tax documents
-- -------------------------------------------------------
CREATE TABLE commercial.invoices (
    id SERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id INTEGER NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    plan_id INTEGER NOT NULL,
    plan_name VARCHAR(255) NOT NULL,
    price_clp DECIMAL(12,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PAID' 
        CHECK (status IN ('PAID', 'PENDING', 'OVERDUE', 'CANCELLED')),
    payment_method VARCHAR(100),
    last_4_digits VARCHAR(4),
    subscription_id INTEGER,
    payment_id INTEGER,
    tax_amount DECIMAL(10,2),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_customer FOREIGN KEY (customer_id) 
        REFERENCES commercial.customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_invoice_plan FOREIGN KEY (plan_id) 
        REFERENCES commercial.plans(id) ON DELETE CASCADE
);

-- ============================================================
-- SCHEMA: agent (AI Agent & Vector Store)
-- Service: agent (:8000)
-- ============================================================

CREATE SCHEMA agent;
COMMENT ON SCHEMA agent IS 'Vector store (PGVector) and AI conversation contexts';

-- -------------------------------------------------------
-- Table: agent.documents
-- Indexed documents for RAG
-- -------------------------------------------------------
CREATE TABLE agent.documents (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    embedding vector(1536),
    source VARCHAR(100),
    commercial_user_id INTEGER,
    indexed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- Table: agent.conversations
-- AI conversation contexts per user
-- -------------------------------------------------------
CREATE TABLE agent.conversations (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    session_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    context JSONB,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------
-- Table: agent.responses
-- Agent response history
-- -------------------------------------------------------
CREATE TABLE agent.responses (
    id SERIAL PRIMARY KEY,
    conversation_id INTEGER NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    model VARCHAR(50),
    tokens INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_response_conversation FOREIGN KEY (conversation_id) 
        REFERENCES agent.conversations(id) ON DELETE CASCADE
);

-- ============================================================
-- CROSS-SCHEMA FOREIGN KEYS
-- ============================================================

-- auth.user_stores → inventory.stores
ALTER TABLE auth.user_stores
    ADD CONSTRAINT fk_user_store_store FOREIGN KEY (store_id)
    REFERENCES inventory.stores(id) ON DELETE CASCADE;

-- sales.cash_shifts → inventory.stores
ALTER TABLE sales.cash_shifts
    ADD CONSTRAINT fk_shift_store FOREIGN KEY (store_id)
    REFERENCES inventory.stores(id) ON DELETE CASCADE;

-- sales.sales → inventory.stores
ALTER TABLE sales.sales
    ADD CONSTRAINT fk_sale_store FOREIGN KEY (store_id)
    REFERENCES inventory.stores(id) ON DELETE CASCADE;

-- sales.pos_cart → inventory.products
ALTER TABLE sales.pos_cart
    ADD CONSTRAINT fk_cart_product FOREIGN KEY (product_id)
    REFERENCES inventory.products(id) ON DELETE CASCADE;


-- ============================================================
-- AUDIT: Triggers and Functions
-- ============================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- auth
CREATE TRIGGER tr_users_updated_at
    BEFORE UPDATE ON auth.users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- inventory
CREATE TRIGGER tr_products_updated_at
    BEFORE UPDATE ON inventory.products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER tr_stock_updated_at
    BEFORE UPDATE ON inventory.stock
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- commercial
CREATE TRIGGER tr_customers_updated_at
    BEFORE UPDATE ON commercial.customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER tr_subscriptions_updated_at
    BEFORE UPDATE ON commercial.subscriptions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER tr_invoices_updated_at
    BEFORE UPDATE ON commercial.invoices
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- agent
CREATE TRIGGER tr_conversations_updated_at
    BEFORE UPDATE ON agent.conversations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
