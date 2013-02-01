# Skinny Elephant's Entity Framework


## Intro
   Simple entity/component/system based framework written in Java.

## Usage

### Core
<p>Core class is base class of this framework, containing pretty much everything required to operate with this framework. Core class contains all systems and managers.</p> 
<br>To initialize Core simply do : </br> 
<br>`Core core= new Core();`</br>
<br>`core.initialize();`</br>

### Entites
<br>Entities basically are containers with components that are used by systems.
<br>1. Usage 
<br>To create new Entity call `core.createEntity()`.
<br>To create new referenced  Entity call `core.createEntity("entityref")`.


### Components
<br>Components are basically just plain java objects containing data used by systems.
<br>1. Usage
<br>For component to be recognized by framework as component, class should be annotated with `@Component`
<br>To add component to entity call `entity.addComponent(new Component)`
<br>To get component from entity call `entity.getComponent(Component.class)`
<br>If component has some resources that requires manual disposal at end of entity life it should implement `Disposable` interface.

### Systems

<br> Systems are are used to manage game logic, for example rendering/physics etc.</b>
<br>Systems can be </br> 
<br>
<br>1. Types</br>
<br>* Default(Entities are passed to system and `processSystem()` method is called allways). </br>
<br>* Passive(No Entities will be passed to it). </br>
<br>* Periodic(System is processed only in specified intervals). </br>
<br>
<br>2. Usage</br>
<br>To designate class as EntitySystem calss should extend `EntitySystem`</br>
<br>To add components that are required by this system, in `initialize()` method call ` addUsedComponent(.class)`
<br>To add this EntitySystem to core simply call `core.addSystem(new EntitySystem)`

<br>For default and periodic non passive systems, entities containing components used by system will passed via method `process(ImmutableSet<Entity> entities)`</br>

### Managers
<br>Managers contains objects that are used by systems but ain't entities for example input processors, cameras, assets etc.

<br>1. Usage
<br> Managers should implement interface `Manager`
<br>To add manager to world call `core.addManager(new Manager)`
