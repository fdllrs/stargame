# Game Design Document: Stellar Harvester (Working Title)

A macro-scale, incremental space factory and resource networking game. The player travels between coexisting star systems in 3D, establishes orbital extraction sites, and designs automated drone logistics to process raw materials. The systems architecture relies on isolated planetary data nodes (local storage) connected by directed logistical edges (drones).

---

## 1. Game Vision & Vibe

* **Vibe:** Mesmerizing, relaxing, and highly satisfying. The core psychological reward stems from observing a sprawling, multi-system machine operating synchronously without player micro-management.
* **Perspective:** 3D space flight (third-person/first-person). The player pilots a ship to navigate celestial bodies. Factory management, node configuration, and logistics routing are handled via UI overlays attached to planetary bodies.
* **Core Philosophy:** Node-based expansion. Planets act as isolated data caches and processing nodes. There is no universal global inventory. The player must build the network bus (drones) to keep resources flowing, prevent buffer overflows, and avoid system bottlenecks.

---

## 2. The Core Loop

1. **Manual Seeding:** The player manually mines raw Metals on a rocky planet and transports them to the Central Orbital Hub to bootstrap initial automation infrastructure.
2. **Node Activation (Extraction):** The player constructs Extractors on a planetary node. These machines continuously populate the planet's localized storage buffer.
3. **Network Routing (Logistics):** The player establishes Drone Hubs, programming directed routes to move specific resources from an origin node's buffer to a destination node's buffer.
4. **In-Place Processing:** The player constructs processing facilities (e.g., Smelters) on designated factory planets. These facilities act as passive consumers, executing state checks on the local storage array and converting inputs into advanced outputs.
5. **Global Progression:** Advanced components are routed back to the Central Orbital Hub to upgrade the ship's Hyperdrive, unlocking procedural generation of new star systems and scaling the logistical network.

---

## 3. Planetary Storage Architecture

Every celestial body functions as an independent state node with strict storage capacities.

* **Buffer Limits:** A baseline planet holds a fixed maximum capacity (e.g., 1,000 units total). If $Storage_{current} \ge Storage_{max}$, all local extraction and processing halts.
* **Silo Expansion:** Constructing Storage Silos increases the maximum buffer size, preventing production stalls during logistical delays.
* **Resource Profiles:**
* **Rocky Planets/Moons:** Rich in Metals.
* **Gas Giants:** Rich in Hydrogen.
* **Ice Giants:** Rich in Water and Hydrogen.
* **Organic Planets:** Rich in Organics and Metals.
* **Stars:** Contain late-game Star Matter.



---

## 4. Drone Logistics Network

Drones act as the connective tissue of the economy, executing asynchronous transport between planetary storage arrays.

* **Route Configuration:** The player defines a Directed Edge by selecting: `Origin Node` $\rightarrow$ `Destination Node` $\rightarrow$ `Item Filter`.
* **Physical Traversal:** Drones spawn as physical entities in 3D space. Throughput (items/minute) is strictly a function of the drone's speed and the Euclidean distance between the nodes.
* **Overflow Handling:** If a drone attempts to push data (cargo) to a destination node whose buffer is full, the drone enters a standby orbit. It will not return to the origin or drop cargo; it waits for the buffer to clear, temporarily reducing the network's active throughput.

---

## 5. In-Place Manufacturing

Factories do not require complex belt routing. They operate via tick-based state evaluations of the planet's inventory.

| Facility Type | Recipe ($Inputs \rightarrow Outputs$) | Function |
| --- | --- | --- |
| **Basic Extractor** | $\emptyset \rightarrow 1 \text{ Metal}$ | Continuously adds raw resources to local storage. |
| **Alloy Smelter** | $2 \text{ Metals} \rightarrow 1 \text{ Alloy}$ | Consumes raw metals; outputs structural alloys. |
| **Chemical Plant** | $1 \text{ Hydrogen} + 1 \text{ Water} \rightarrow 1 \text{ Coolant}$ | Combines local gases and liquids. |
| **Engine Fabricator** | $5 \text{ Alloys} + 2 \text{ Coolant} \rightarrow 1 \text{ Thruster}$ | Manufactures high-tier progression components. |

> **Execution Logic:** Every server tick, the facility queries the local inventory. If $Inventory[Input] \ge Required$, it deducts the inputs and increments the output array. The player only manages the logistics supplying the inputs.

---

## 6. Civilization State Machine & Diplomacy

Planets have a chance to host procedural civilizations. These act as logistical modifiers. Their disposition is tracked on a continuous scale of 1 to 10.

### Disposition States

| Value Range | State | Mechanical Impact |
| --- | --- | --- |
| **1 - 3** | **Hostile** | Planetary UI is **Locked**. Player cannot build or extract. A massive one-time resource tribute (Cultural Project) is required to unlock. |
| **4 - 6** | **Wary** | UI is unlocked. Player can build, but local machinery suffers a $-20\%$ efficiency penalty due to local friction. |
| **7 - 8** | **Cooperative** | Baseline operations. $1.0\times$ multiplier to all local machinery. |
| **9 - 10** | **Allied** | Optimal operations. Civilization provides a $+25\%$ speed multiplier to local extraction and processing. |

### The "Forced Relocation" (Eviction) Mechanic

Players can bypass the resource tribute by forcefully clearing a Hostile node using late-game resources (e.g., Orbital Deterrents).

* **The Action:** Pays a flat, heavy cost. The node instantly becomes player-owned.
* **The Proximity Penalty (Ripple Effect):** Eviction triggers a disposition drop in neighboring nodes based on graph distance:
* *Same Star System:* $-3$ Disposition to all inhabited planets.
* *Adjacent Star System (1 Warp Hop):* $-1$ Disposition to all inhabited planets.
* *$\ge 2$ Warp Hops:* $0$ impact.


* **Passive Recovery:** To prevent permanent network gridlock, disposition organically drifts. A Hostile state (1-3) left completely alone will slowly regenerate to Wary (4) over real-time hours. Alternatively, establishing an automated drone route that delivers high-value resources directly into their local storage accelerates disposition growth.

---

## 7. The Central Orbital Hub & Progression

The player's progression is anchored to a massive, upgradeable space station orbiting the starting planet in the first system.

* **The Core Sink:** This structure is the ultimate destination for all advanced manufacturing pipelines. Drones must route Thrusters, Cores, and Alloys here.
* **Hyperdrive Scaling:** The player upgrades their ship's Hyperdrive directly from the Hub's interface using the accumulated resources.
* **Infinite Expansion:** Upgrading the Hyperdrive increases ship acceleration, top speed, and unlocks the procedural generation of further star systems, introducing rarer resources and more complex civilization puzzles.