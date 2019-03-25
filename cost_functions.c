void computeMappingCostCMUHOUSEAVertices()
{
	double nodeCost = 100000; //This is a predefined param
	double alpha = 0.5;
	double cost, val, val1;

	//Allocate memory for matcostvertices
	matCostVertices = (double **)malloc(sizeof(double *)*G.nVb);

	for (unsigned int i = 0; i < G.nVb; i++)
	{
		matCostVertices[i] = (double *)malloc(sizeof(double)*G1.nVb);
		for (unsigned int j = 0; j < G1.nVb; j++)
		{
			cost = 0.0;

			//to handle case assigning obj N to itself
			if (i == G.nV && j == G1.nV)
			{
				cost = 0; //INT_MAX;
				//break;
			}
			else
			{
			
				//deletion form G'or deleting from G
				if (i == G.nV || j == G1.nV)
				{
					cost = alpha * nodeCost;
				}
				else //substitution
				{
					// norm L1 for set of attributes
					val = 0.0;
					val1 = 0.0;

					//loop over attr
					for (unsigned int k = 2; k < G.nbAttributesVertex; k++)
					{
						val = parseDouble(G.vertices[i].keyValue[k].Value);
						val1 = parseDouble(G1.vertices[j].keyValue[k].Value);
						cost += computeDistance(val, val1);
					}

					cost = alpha * cost;
				}
			}

			//put the val in mat
			matCostVertices[i][j] = cost;
		}
	}
}

void computeMappingCostCMUHOUSEAEdges()
{
	double edgeCost = 1; //This is a predefined param
	double alpha = 0.5;
	double cost;

	//Allocate memory for matcostedges
	matCostEdges = (double **)malloc(sizeof(double *)*G.nEb);

	for (unsigned int i = 0; i < G.nEb; i++)
	{
		matCostEdges[i] = (double *)malloc(sizeof(double)*G1.nEb);
		for (unsigned int j = 0; j < G1.nEb; j++)
		{
			cost = 0.0;

			//to handle case assigning obj N to itself
			if (i == G.nE && j == G1.nE)
			{
				cost = 0; //INT_MAX;
				//break;
			}
			else
			{
				//deletion form G'or deleting from G
				if (i == G.nE || j == G1.nE)
				{
					cost = (1 - alpha) * edgeCost;
				}
				else //substitution of edges
				{
					cost = 0;
				}
			}

			//put the val in mat
			matCostEdges[i][j] = cost;
		}
	}
}

inline double computeDistance(double x, double y)
{
	return abs(x - y);
}