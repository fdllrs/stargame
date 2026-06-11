# Game Design Document: Stellar Harvester (Working Title)

A macro-scale, incremental space factory and resource networking game. The player travels between procedurally generated
star systems in 3D, establishes orbital extraction sites, and designs automated drone logistics to process raw
materials. The player forges relationships with alien civilizations to build a self-sustaining interstellar empire
culminating in the construction of a Dyson Sphere. The systems architecture relies on isolated planetary data nodes (
local storage) connected by directed logistical edges (drones and civ carriers).

---

## 1. Game Vision & Vibe

* **Vibe:** Mesmerizing, relaxing, and highly satisfying. The core psychological reward stems from observing a
  sprawling, multi-system machine operating synchronously without player micro-management.
* **Perspective:** 3D space flight (third-person/first-person). The player pilots a ship to navigate celestial bodies.
  Factory management, node configuration, and logistics routing are handled via UI overlays attached to planetary
  bodies.
* **Core Philosophy:** Node-based expansion. Planets act as isolated data caches and processing nodes. There is no
  universal global inventory. The player must build the network bus (drones, carrier civs) to keep resources flowing,
  prevent buffer overflows, and avoid system bottlenecks.

---

## 2. The Core Loop

1. **Local Isolation & Seeding:** The player starts in a curated starting system near a resource-rich planet. The player
   manually mines raw resources (like Metals) on the starting planet to build initial automation infrastructure, an
   Orbital Hub, and a Research Laboratory.
2. **Node Activation (Extraction):** The player constructs Extractors on a planetary node to continuously populate the
   planet's localized storage buffer.
3. **In-Place Processing & Science:** The player constructs manufacturing facilities (e.g., Smelters) and **Research
   Laboratories**. Labs consume local resources to generate physical **Science Products** (Geological Data, Bio-Data,
   etc.) in local storage.
4. **Physical Research Delivery:** Science Products are transported to the Hub — initially by the player's ship, later
   by drones or allied Carrier civilizations — to unlock engine and cargo upgrades.
5. **Expansion & Network Growth:** Upgrading to Fusion Engines (Tier 1) opens inner system travel. Warp Drive enables
   cross-system exploration. The player establishes logistics pathways and expands their production network across
   procedurally generated star systems.
6. **Civilization Integration:** The player encounters alien civilizations on inhabited planets. By investing in
   cultural upgrades, the player unlocks trade deals, logistics partnerships, and scientific collaboration.
7. **Megastructure Endgame:** The player works toward constructing a Dyson Sphere around a chosen star, requiring rare
   resources from multiple star systems and a fully operational interstellar logistics network.

---

## 3. Planetary Storage Architecture

Every celestial body functions as an independent state node with a single shared storage buffer.

### Planetary Buffer

All resources on a planet — raw materials, processed goods, and incoming cargo — occupy one unified buffer with a hard
unit cap (e.g., 1,000 units by default).

* **What goes in:** Resources produced by Extractors, outputs from processing facilities, and incoming cargo delivered
  by drones or the player's ship.
* **What pulls from it:** Facilities consume resources from the buffer as inputs. Drones pull resources from the buffer
  for transport.
* **When it fills up:** Extractors halt — no new raw resources can be added. Facilities continue to run as long as their
  required inputs are still present in the buffer (processing consumes some resources and adds others, keeping the level
  in flux). Incoming drone deliveries are blocked; the delivering drone enters standby orbit until space opens up (see
  Section 5).
* **Deadlock risk:** If a processed good accumulates faster than drones remove it, it will eventually crowd out raw
  input resources, starving downstream facilities. The player resolves this by establishing or improving drone routes on
  that planet. A buffer overflow notification fires early enough to act before a full stall.

### Silo Expansion

Constructing **Storage Silos** increases the buffer cap, providing headroom during logistical delays or periods of high
throughput.

### Resource Flow

```
[Extractor] ──────────────────────────────────────────────┐
                                                           │
                                                           ▼
                                               [Planetary Buffer]
                                               (unified, capped)
                                                     │     ▲
                            Facility pulls inputs ◄──┘     │
                                     │                     │
                                     ▼                     │
                            [Facility runs recipe]         │
                                     │                     │
                      Output added back to buffer ─────────┘
                                     
                         Drone pulls from buffer
                                     │
                                     ▼
                    [Destination Planet's Buffer]
```

### Resource Profiles (Placeholder)

Different celestial body types yield different raw resources. These profiles determine what can be extracted and which
facilities are worth building at each node.

| Body Type               | Primary Resources          | Notes                                                                                  |
|-------------------------|----------------------------|----------------------------------------------------------------------------------------|
| **Rocky Planet / Moon** | Metals                     | Core early-game resource.                                                              |
| **Gas Giant**           | Hydrogen                   | Fuel for Fusion Reactors and Chemical Plants.                                          |
| **Ice Giant**           | Water, Hydrogen            | Inputs for cooling and chemical chains.                                                |
| **Organic Planet**      | Organics, Metals           | Required for biological science production.                                            |
| **Star**                | Star Matter, Exotic Energy | Late-game only. Requires Thermal Shield II–III.                                        |
| **Any (Rare Deposit)**  | Planet-unique rare variant | Discoverable via exploration; required for advanced factory chains and megastructures. |

Resource types and yields are placeholder and subject to expansion during content design.

---

## 4. Planetary Energy Grid

Every planet operates an independent power grid. Facilities require energy to function; drones do not.

### Capacity & Brownout System

* Each facility has a fixed **Energy Demand** (e.g., Extractor = 2 MW, Smelter = 5 MW).
* Each generator has a fixed **Energy Output**.
* There is no hard construction block based on energy. The player may always build new facilities.
* If a planet's total machine demand exceeds its installed capacity, the planet enters a **Brownout** state: all
  facilities operate at reduced efficiency proportional to available power.

$$Efficiency = \frac{Capacity_{installed}}{Demand_{total}}$$

* A notification is dispatched when a planet enters brownout, giving the player time to respond by constructing
  additional generators.

### Generator Tiers (Placeholder)

| Generator                | Fuel / Requirement   | Output      | Notes                                                         |
|--------------------------|----------------------|-------------|---------------------------------------------------------------|
| **Solar Array**          | None                 | Low         | Free to run; output scales with proximity to the local star.  |
| **Fusion Reactor**       | Hydrogen             | Medium-High | Mid-game workhorse; consumes Gas Giant resources.             |
| **Antimatter Plant**     | Exotic Matter        | Very High   | Late-game; requires rare resource inputs.                     |
| **Dyson Swarm / Sphere** | Star (Megastructure) | Massive     | End-game; provides enormous energy to the entire host system. |

Generator types, values, and fuel requirements are placeholder and subject to change during content design.

---

## 5. Drone Logistics Network

Drones are the primary logistics layer within a star system. They do not cross star system boundaries.

* **Scope:** Drones operate exclusively within a single star system. Cross-system logistics are handled by Carrier
  civilizations (see Section 7) or the player's ship.
* **Route Configuration:** The player defines a Directed Edge by selecting: `Origin Node` → `Destination Node` →
  `Item Filter`.
* **Batch Mode (Optional Toggle):** Each route can optionally be configured to wait until a minimum quantity is
  available at the origin before departing. This prevents drone spam on low-throughput routes and gives the player
  direct control over throughput density.
* **Physical Traversal:** Drones spawn as physical entities in 3D space. Throughput (items/minute) is strictly a
  function of drone speed and the Euclidean distance between nodes.
* **Overflow Handling:** If a drone arrives at a destination whose buffer is full, it enters a standby orbit. It waits
  for the buffer to clear before unloading and does not drop or return cargo. A notification is dispatched when a drone
  enters standby.

---

## 6. In-Place Manufacturing

Factories do not require complex belt routing. They operate via tick-based state evaluations of the planet's inventory.

| Facility Type           | Recipe ($Inputs \rightarrow Outputs$)                                 | Function                                                     |
|-------------------------|-----------------------------------------------------------------------|--------------------------------------------------------------|
| **Basic Extractor**     | $\emptyset \rightarrow 1 \text{ Metal}$                               | Continuously adds raw resources to local storage.            |
| **Alloy Smelter**       | $2 \text{ Metals} \rightarrow 1 \text{ Alloy}$                        | Consumes raw metals; outputs structural alloys.              |
| **Chemical Plant**      | $1 \text{ Hydrogen} + 1 \text{ Water} \rightarrow 1 \text{ Coolant}$  | Combines local gases and liquids.                            |
| **Engine Fabricator**   | $5 \text{ Alloys} + 2 \text{ Coolant} \rightarrow 1 \text{ Thruster}$ | Manufactures high-tier progression components.               |
| **Research Laboratory** | Planet-dependent inputs $\rightarrow 1 \text{ Science Product}$       | Consumes resources to synthesize transportable science data. |

> **Execution Logic:** Every server tick, the facility queries the planetary buffer. If $Buffer[Input] \ge Required$, it
> deducts the inputs and deposits the outputs back into the same buffer. Drones then pull finished goods from the buffer
> for transport. Facilities only run at full speed when the planet's energy supply meets demand; brownout reduces all
> facility speeds proportionally.

Recipes and facility types are placeholder and subject to expansion during content design.

### Orbital Structures

Orbital structures are player-built constructions that orbit a celestial body rather than sitting on a planet surface.
They do not consume planetary energy and are not subject to storage buffer limits.

| Structure                  | Orbit Target | Function                                                                                                                                                                                                                                                                                                                                                     |
|----------------------------|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **System Command Hub**     | Star         | Unlocks remote management of all planets within the host star system. While physically present anywhere in the system, the player can access any planet's UI overlay, configure drone routes, and queue construction without flying to each body individually. Requires **Thermal Shield I** to place, since the player must fly to star orbit to deploy it. |
| **Orbital Mega-Refinery**  | Planet       | Processes resources at 10× the throughput of surface facilities.                                                                                                                                                                                                                                                                                             |
| **Orbital Research Array** | Planet       | Replaces the need for surface Research Laboratories in that system; generates all science types at high output.                                                                                                                                                                                                                                              |

---

## 7. Civilization System

Planets have a chance to host one of four alien civilization archetypes. Each civilization has a visible type from first
contact. There is no disposition scale; civs are not inherently hostile. The starting system is curated to ensure no
civilization blocks critical early-game resources or progress.

Players invest in **Cultural Tributes** (a one-time resource package) or **Cultural Buildings** (permanent structures
that passively accelerate level progression) to raise a civilization's level and unlock capabilities. Leveling costs and
rates can be reduced via the Diplomacy research branch.

---

### Civ Archetypes

#### 🛒 Traders

Exchange resources with the player.

| Level | Capability                                       |
|-------|--------------------------------------------------|
| **0** | 1 standing offer; poor exchange rates.           |
| **1** | 2 standing offers; standard exchange rates.      |
| **2** | 3 standing offers; discounted (favorable) rates. |
| **3** | 3 offers at best rates + 1 bonus offer slot.     |

**Offer System:**

* The civilization defines its offers based on its planet's resource profile (e.g., a Trader on a Gas Giant offers
  Hydrogen in exchange for Alloys).
* All offers refresh once per in-game day.
* The player may propose a **counter-offer** at any time. Counter-offers are always accepted but execute at a worse
  exchange rate than the civ's standing offers.
* A **daily reroll** (unlockable via the Diplomacy research branch) allows the player to refresh a specific Trader's
  offers early once per day.

---

#### 🚚 Carriers

The only automated logistics option for cross-system transport. Carriers fill the role that drones cannot: moving cargo
between star systems.

| Level | Capability                                           |
|-------|------------------------------------------------------|
| **0** | No routes available.                                 |
| **1** | 1 carrier route; short max distance; high cargo tax. |
| **2** | 2 carrier routes; medium distance cap; reduced tax.  |
| **3** | 3 carrier routes; long distance cap; minimum tax.    |

**Carrier Route System:**

* Routes are defined by the player using the same `Origin → Destination → Item Filter` interface as drones.
* Carriers execute the route autonomously and deduct their fee — a percentage of the cargo carried — before delivery.
* Carrier routes can transport Science Products, making them a viable alternative to player-piloted science delivery
  from the mid-game onward.
* The distance cap scales with civ level and can be further extended via the Diplomacy research branch.

---

#### 🔬 Scientists

Autonomously generate Science Products.

| Level | Capability                                                                                                      |
|-------|-----------------------------------------------------------------------------------------------------------------|
| **0** | No science output.                                                                                              |
| **1** | Produces 1 science type at a slow rate.                                                                         |
| **2** | Produces 2 science types at a normal rate.                                                                      |
| **3** | Produces 2–3 science types at a high rate. Output type range can be expanded via the Diplomacy research branch. |

Science Products generated by Scientists are placed into the planet's local storage and must be transported to the Hub
via drone, Carrier route, or the player's ship.

---

#### 🏔️ Isolationists

Culturally closed civilizations. Before any interaction is possible, the player must complete a **Request Chain** to
earn initial access. After access is granted, normal leveling applies.

**Request Chain (Unlock Gate):**
Each Isolationist civ presents a procedurally determined sequence of 3–4 resource requests, revealed one at a time. The
player must fulfill each request in order by delivering the specified resources to the planet. Completing the full chain
grants access at **Level 0**. The specific sequence differs per civ, making each Isolationist feel unique.

| Level | Capability                                                                         |
|-------|------------------------------------------------------------------------------------|
| **0** | *(Unlocked via Request Chain)* Extraction at 50% yield; no construction permitted. |
| **1** | Extraction at 75%; basic facilities permitted.                                     |
| **2** | Full extraction yield; all facilities permitted.                                   |
| **3** | Full access; planet-wide passive efficiency bonus on all machinery.                |

---

## 8. Research Tree & Hub Progression

Progression is governed by physical science delivery and ship upgrades anchored to the Central Orbital Hub. The research
tree has a fixed structure (the same each game) and is organized into specialized branches. Each branch contains
multiple tiers; each tier unlocks exactly one capability.

### Science Delivery

Science Products can be transported to the Hub by:

* The player's ship (early game).
* Automated drones (mid-game, intra-system only).
* Carrier civilizations (mid-to-late game, cross-system).

The Hub accepts science deliveries from any source identically.

### Physicalized Science & Laboratories

| Science Type                   | Source              | Input Resources  |
|--------------------------------|---------------------|------------------|
| **Geological / Physical Data** | Rocky Planet Labs   | Metals           |
| **Biological / Sample Data**   | Organic Planet Labs | Organics + Water |
| **Gas / Atmospheric Data**     | Gas Giant Labs      | Hydrogen         |
| **Cryo-Physics Data**          | Ice Giant Labs      | Water or Coolant |

---

### Research Branches

#### 🚀 Propulsion

Focused on ship mobility, exploration range, cargo capacity, and hull survivability. Hull resistance tiers act as hard
access gates — the player's ship is physically blocked from entering heat zones beyond its current rating.

| Tier | Unlock                                                                                                                                                                                                                             |
|------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1    | Fusion Engines ($MaxSpeed = 150$, $Acceleration = 50$)                                                                                                                                                                             |
| 2    | Cargo Hold Expansion I (200 → 500 units)                                                                                                                                                                                           |
| 3    | Warp Drive (turbo boost for cross-system travel)                                                                                                                                                                                   |
| 4    | **Thermal Shield I** — Allows approach to inner rocky planets in close stellar orbit. Required to deploy the System Command Hub (star orbit). Planets in this zone yield higher Solar Array output and may contain rare resources. |
| 5    | Cargo Hold Expansion II (500 → 1,500 units)                                                                                                                                                                                        |
| 6    | Hyper-Warp (near-instant system traversal)                                                                                                                                                                                         |
| 7    | **Thermal Shield II** — Allows close stellar orbit. Enables Star Matter and Exotic Energy harvesting directly from the star.                                                                                                       |
| 8    | Cargo Hold Expansion III (1,500 → 3,000+ units)                                                                                                                                                                                    |
| 9    | **Thermal Shield III** — Allows direct stellar proximity. Required to construct Dyson Swarm and Dyson Sphere megastructures.                                                                                                       |
| …    | Further tiers TBD                                                                                                                                                                                                                  |

The ship can also be visually customized with a color/livery of the player's choice.

**Star Proximity Perks:** Planets and structures in close stellar orbit benefit from elevated Solar Array output
proportional to proximity. The star itself yields Star Matter and Exotic Energy, gated behind Thermal Shield II and III
respectively.

---

#### ⚙️ Automation

Focused on factory throughput and drone network efficiency.

| Tier | Unlock                                                                      |
|------|-----------------------------------------------------------------------------|
| 1    | Drone speed upgrade I                                                       |
| 2    | Batch mode for drone routes                                                 |
| 3    | Drone speed upgrade II                                                      |
| 4    | Advanced factory recipe tier I                                              |
| 5    | Parallel processing (facilities operate on multiple batches simultaneously) |
| …    | Further tiers TBD                                                           |

---

#### ⚡ Energy

Focused on power generation, consumption efficiency, and megastructure access.

| Tier | Unlock                                                     |
|------|------------------------------------------------------------|
| 1    | Fusion Reactor access                                      |
| 2    | Generator efficiency upgrade I                             |
| 3    | Power consumption reduction I (all facilities −10% demand) |
| 4    | Antimatter Plant access                                    |
| 5    | Power consumption reduction II                             |
| 6    | Dyson Swarm access (prerequisite to Dyson Sphere)          |
| …    | Further tiers TBD                                          |

---

#### 🤝 Diplomacy

Focused on civilization relationships, trade economics, and cultural tools. Each tier unlocks exactly one capability.

| Tier         | Unlock                                                                                  |
|--------------|-----------------------------------------------------------------------------------------|
| 1            | Reduced cultural tribute cost                                                           |
| 2            | Faster civ level progression rate                                                       |
| 3            | Free daily trade reroll (per Trader civ)                                                |
| 4            | Carrier cargo tax % reduced                                                             |
| 5            | Bulk trading (trade larger quantities per deal)                                         |
| 6            | Carrier route distance cap increased                                                    |
| 7            | Unlock second simultaneous Carrier route per Carrier civ                                |
| 8            | Cultural Monument building unlocked (greatly accelerates civ level on that planet)      |
| 9            | Scientists produce +1 additional science type                                           |
| **Capstone** | Max-level civs passively contribute a small resource trickle to local planetary storage |

---

## 9. Megastructures

Megastructures are large-scale, multi-phase construction projects representing the apex of the player's technological
and logistical capability. They require rare resources from distant star systems and a well-optimized production network
to complete.

### Milestone Megastructures

Intermediate constructions that provide powerful functional upgrades:

| Megastructure              | Function                                                                                                                        |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| **Interstellar Relay**     | Extends Carrier civ route range across star systems. Placed at a point between two systems.                                     |
| **Orbital Mega-Refinery**  | Orbits a planet; processes resources at 10× the throughput of standard facilities. (See also Section 6 — Orbital Structures.)   |
| **Orbital Research Array** | Orbits a planet; replaces the need for surface Research Laboratories in that system. (See also Section 6 — Orbital Structures.) |

### Sci-Fi Tier Megastructures

Speculative, high-cost megaprojects representing the far frontier of in-game technology:

| Megastructure          | Function                                                                                                                                                                                                                                     |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Wormhole Generator** | Creates a permanent, near-instant logistics corridor between two specified star systems.                                                                                                                                                     |
| **Stellar Engine**     | Repositions a star within the galaxy map, enabling more optimal system connectivity.                                                                                                                                                         |
| **Planet Mover**       | Relocates a planet within its own system to optimize resource proximity to other nodes.                                                                                                                                                      |
| **Dyson Swarm**        | Partial stellar energy capture. Prerequisite to the Dyson Sphere. Provides large energy output to the host system. Requires **Thermal Shield III**.                                                                                          |
| **Dyson Sphere**       | Complete stellar energy capture. Provides massive energy output to the host system. Completing and activating a Dyson Sphere triggers the **credit roll** (see Section 10). Requires **Thermal Shield III** and full Energy branch research. |

The player may choose any star system as the site for their Dyson Sphere.

---

## 10. Win Condition & Late-Game Arc

### Win Condition

The player's long-term goal is to **construct and activate a Dyson Sphere** around any star of their choosing.
Completion requires:

* Advanced research across all branches.
* Thermal Shield III (Propulsion branch).
* Rare resource extraction from multiple distant star systems.
* A fully operational interstellar logistics network capable of routing materials to the construction site.

Upon activation, the game displays the credit roll. The player may then continue playing in sandbox mode with all
progress intact.

### Late-Game Player Activities

The late game is driven by active expansion and escalating optimization challenges:

* **Multi-hop supply chains:** High-tier recipes require resources from multiple star systems simultaneously, making
  cross-system logistics the primary optimization challenge.
* **Civ investment:** Pushing civilizations to max level across multiple systems for passive production bonuses and
  dense Carrier route coverage.
* **Megastructure construction:** Sequencing and building intermediate megastructures as stepping stones toward the
  Dyson Sphere.
* **Rare resource discovery:** Exploring distant systems to locate unique resource deposits required for advanced
  factory chains and megastructures.
* **Network optimization:** Redesigning drone and Carrier networks as production scale increases — resolving
  bottlenecks, expanding throughput, and managing buffer capacity across dozens of active nodes.
* **Thermal frontier expansion:** Pushing Thermal Shield tiers to open access to star-adjacent planets, stellar
  resources, and ultimately Dyson Sphere construction.

---

## 11. Notification System

The game dispatches non-intrusive alerts for the following events. Notifications are displayed in a persistent log and
optionally as contextual indicators on planetary bodies in the 3D world.

| Event                          | Trigger                                                                                                        |
|--------------------------------|----------------------------------------------------------------------------------------------------------------|
| **Brownout Warning**           | A planet's machine demand has exceeded installed energy capacity; all facilities are running below full speed. |
| **Buffer Overflow**            | A planet's storage buffer is full; extraction and processing are halted.                                       |
| **Drone Standby**              | One or more drones are waiting for a destination buffer to clear.                                              |
| **Science Product Ready**      | A Research Laboratory or Scientist civ has science products in local storage awaiting transport.               |
| **Civ Level Up Available**     | Cultural tribute threshold has been reached; a civ's level can be increased.                                   |
| **Isolationist Request Ready** | An Isolationist civ has issued the next request in their unlock chain.                                         |
| **Trader Offer Refresh**       | A Trader civ's standing offers have been refreshed for the day.                                                |
| **Research Unlock Available**  | The Hub has received sufficient science to unlock a new research node.                                         |
| **Megastructure Milestone**    | A megastructure construction phase has been completed.                                                         |