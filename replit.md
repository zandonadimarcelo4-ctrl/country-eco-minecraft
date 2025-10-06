# Country System Mod for Minecraft 1.21

## Overview
A comprehensive Minecraft Fabric mod that implements a full country system with governments, citizens, military, colonies, economy, and diplomacy. This mod combines country management with an integrated economic system including CPF (Brazilian tax ID), PIX transfers, loans, investments, and credit scores.

## Project Architecture

### Core Systems
1. **Country System** - Create and manage countries with different government types (Republic, Monarchy, Dictatorship, Tribe)
2. **Citizenship** - Three-tier system (Visitor, Resident, Citizen) with progressive permissions
3. **Military** - Rank-based system (Soldier, Captain, General) with combat bonuses
4. **Colonies** - Establish settlements or exploration colonies with independence mechanics
5. **Economy** - Complete financial system with accounts, transactions, loans, and investments
6. **Territory** - Chunk-based claiming with visual boundaries

### Government Types
- **Republic**: Democratic leadership with citizen voting
- **Monarchy**: Hereditary succession with heir system
- **Dictatorship**: Force-based leadership changes only
- **Tribe**: Combat-determined leadership

### Economy Features
- **PlayerAccount**: Individual economic accounts for all players
- **CPF System**: Brazilian-style tax ID with auto-generation and validation
- **PIX Transfers**: Instant money transfers between players
- **Loans**: Borrow money with interest rates and repayment schedules
- **Investments**: Grow wealth over time with configurable rates
- **Credit Score**: Dynamic scoring (300-850) based on financial behavior

### PvP Takeover Mechanics
1. Kill the country leader
2. Capture the flag within 5-minute window
3. Become the new leader
4. Cooldown prevents immediate re-attacks

## Project Structure

```
src/main/java/com/countrymod/
├── CountryMod.java                 # Main mod initialization
├── CountryModClient.java           # Client-side initialization
├── model/                          # Data models
│   ├── Country.java
│   ├── Colony.java
│   ├── Citizen.java
│   ├── GovernmentType.java
│   ├── CitizenshipLevel.java
│   ├── MilitaryRank.java
│   ├── Territory.java
│   └── Treasury.java
├── economy/                        # Economy system
│   ├── PlayerAccount.java
│   ├── Transaction.java
│   ├── Loan.java
│   ├── Investment.java
│   └── ScoreManager.java
├── manager/                        # Game managers
│   ├── CountryManager.java
│   ├── EconomyManager.java
│   └── DataPersistence.java
├── item/                           # Custom items
│   ├── ModItems.java
│   ├── CountryFlagItem.java
│   └── ColonyFlagItem.java
├── client/gui/                     # GUI screens
│   ├── CountryCreationScreen.java
│   ├── ColonyCreationScreen.java
│   └── CountryHudOverlay.java
├── event/                          # Event handlers
│   └── PvPEventHandler.java
├── util/                           # Utilities
│   ├── CPFGenerator.java
│   └── CPFValidator.java
└── mixin/                          # Mixins
    └── PlayerEntityMixin.java
```

## Recent Changes
- **2025-10-05**: Complete economy and feature expansion
  - Fixed all compilation errors (Java 21, item classes, PvP handlers)
  - Added comprehensive Banking System with credit scores, loan management, and savings accounts
  - Implemented Auction House for competitive item bidding with auto-expiration
  - Integrated Secure Trade System with anti-scam protections
  - Added Tax System for country revenue generation
  - Created War System for country conflicts with territory stakes
  - Built Alliance System for diplomatic relationships
  - Set up automated build workflow with Gradle
  - All systems fully functional with REI$ currency integration

## Development Status

### Completed
- ✅ Core data models (Country, Colony, Citizen, Military)
- ✅ Economy system (Accounts, CPF, Transactions, Loans, Investments)
- ✅ Country and economy managers
- ✅ Advanced Banking System (credit scores, loan applications, savings accounts)
- ✅ Auction House (competitive bidding, auto-expiration, seller fees)
- ✅ Secure Trading System (anti-scam protections, confirmation flow)
- ✅ Tax System (configurable rates, collection, treasury management)
- ✅ War System (declarations, territory stakes, alliances)
- ✅ Alliance System (diplomatic relationships, betrayal tracking)
- ✅ Basic GUI screens
- ✅ Event handlers for PvP and player connections
- ✅ JSON persistence framework
- ✅ Build configuration and Gradle workflow

### In Progress
- 🔨 Network packet system for client-server communication
- 🔨 Visual territory boundaries with particles

### Planned
- ⏳ Colony independence negotiations
- ⏳ HUD overlay with real-time information
- ⏳ Admin commands for server management
- ⏳ Dynamic world events
- ⏳ Morale and reputation systems

## Building the Mod

This is a Minecraft Fabric mod for version 1.21. To build:

```bash
./gradlew build --no-daemon
```

The compiled JAR will be in `build/libs/`

## Technical Details

- **Minecraft Version**: 1.21
- **Fabric Loader**: 0.15.11+
- **Fabric API**: 0.100.4+1.21
- **Java Version**: 21
- **Mappings**: Yarn 1.21+build.9

## Data Persistence

All data is saved to `<world>/countrymod_data/countries.json` including:
- Countries and their properties
- Citizens and membership
- Military rosters
- Colonies
- Territory claims
- Player economic accounts (future integration)

Data is automatically saved on server shutdown and loaded on startup.

## Notes

This mod is designed with modularity in mind to allow future expansions. All major systems have placeholder comments indicating where additional features can be added.
