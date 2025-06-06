CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE restaurant (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tiny_id TEXT UNIQUE NOT NULL,
    restaurant_name TEXT NOT NULL,
    address_line1 TEXT,
    city TEXT,
    state TEXT,
    pincode TEXT,
    latitude DECIMAL,
    longitude DECIMAL,
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ui_template TEXT
);

CREATE TABLE cuisine_type (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE restaurant_cuisine_type_mapping (
    restaurant_id UUID REFERENCES restaurant(id) ON DELETE CASCADE,
    cuisine_type_id UUID REFERENCES cuisine_type(id) ON DELETE CASCADE,
    PRIMARY KEY (restaurant_id, cuisine_type_id)
);

CREATE TABLE restaurant_timing (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id UUID REFERENCES restaurant(id) ON DELETE CASCADE,
    day_of_week TEXT CHECK (day_of_week IN ('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun')),
    open_time TIME,
    close_time TIME
);

CREATE TABLE dish_category (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id UUID REFERENCES restaurant(id) ON DELETE CASCADE,
    category_name TEXT NOT NULL,
    parent_id UUID REFERENCES dish_category(id) ON DELETE CASCADE,
    UNIQUE (restaurant_id, category_name, parent_id)
);

CREATE TABLE dish (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id UUID REFERENCES restaurant(id) ON DELETE CASCADE,
    dish_name TEXT NOT NULL,
    dish_category_id UUID REFERENCES dish_category(id) ON DELETE SET NULL,
    price NUMERIC(10, 2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dish_tag (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE dish_tag_mapping(
    dish_id UUID REFERENCES dish(id) ON DELETE CASCADE,
    tag_id UUID REFERENCES dish_tag(id) ON DELETE CASCADE,
    PRIMARY KEY (dish_id, tag_id)
);

CREATE TABLE customization_group (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    dish_id UUID REFERENCES dish(id) ON DELETE CASCADE,
    group_name TEXT,
    type TEXT -- should be either 'direct' or 'extra', differentiating between variants of same dish (hot/cold latte) and sides (fries, coke, etc)
);

CREATE INDEX idx_customization_group_dish ON customization_group(dish_id);

CREATE TABLE customization_option (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    group_id UUID REFERENCES customization_group(id) ON DELETE CASCADE,
    option_name TEXT,
    extra_price NUMERIC(10, 2)
);

CREATE INDEX idx_customization_option_group ON customization_option(group_id);

-- Indexes for restaurants table
CREATE INDEX idx_restaurant_location ON restaurant(city, state, latitude, longitude);
CREATE INDEX idx_timing_restaurant ON restaurant_timing(restaurant_id);
CREATE INDEX idx_category_restaurant ON dish_category(restaurant_id);
