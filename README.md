# shulker-portal

Initially just some features related to Shulkers going through nether portals, now featuring arbitrary other modifications that I'm too lazy to make another mod for.

Use along side base [fabric-carpet](https://github.com/gnembon/fabric-carpet) mod for the same minecraft version.


# Shulker Portal Features

## enableShulkerPortalPositioningFix
Shulkers will set their position to that of the nether portal when going through a nether portal.
* Type: `boolean`  
* Default value: `true`  
* Required options: `true`, `false`  
* Categories: `BUGFIX`, `ale`  
  
## enableShulkerPortalDebugging
Enables some debugging features when Shulkers calculate their nether portal transit. Access the gathered data using the `ale` command.
* Type: `boolean`  
* Default value: `false`  
* Required options: `true`, `false`  
* Categories: `ale`  

## enable117RaiderDiscoveryMechanic
Reverts raid recruitment mechanics back to their 1.17 versions, allowing Raid farms built in those versions to continue working. A copy-paste of code written by Fallen_Breath.
* Type: `boolean`
* Default value: `false`
* Required options: `true`, `false`
* Categories: `CREATIVE`, `ale`
