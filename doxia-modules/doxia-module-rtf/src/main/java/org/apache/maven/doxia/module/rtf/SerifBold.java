package org.apache.maven.doxia.module.rtf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * @version $Id$
 */
class SerifBold
    extends FontMetrics
{
    static final CharMetrics[] metrics = {new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 333, 0, 81, -13, 251, 691 ), new CharMetrics( 555, 0, 83, 404, 472, 691 ),
        new CharMetrics( 500, 0, 4, 0, 496, 700 ), new CharMetrics( 500, 0, 29, -99, 472, 750 ),
        new CharMetrics( 1000, 0, 124, -14, 877, 692 ), new CharMetrics( 833, 0, 62, -16, 787, 691 ),
        new CharMetrics( 333, 0, 79, 356, 263, 691 ), new CharMetrics( 333, 0, 46, -168, 306, 694 ),
        new CharMetrics( 333, 0, 27, -168, 287, 694 ), new CharMetrics( 500, 0, 56, 255, 447, 691 ),
        new CharMetrics( 570, 0, 33, 0, 537, 506 ), new CharMetrics( 250, 0, 39, -180, 223, 155 ),
        new CharMetrics( 333, 0, 44, 171, 287, 287 ), new CharMetrics( 250, 0, 41, -13, 210, 156 ),
        new CharMetrics( 278, 0, -24, -19, 302, 691 ), new CharMetrics( 500, 0, 24, -13, 476, 688 ),
        new CharMetrics( 500, 0, 65, 0, 442, 688 ), new CharMetrics( 500, 0, 17, 0, 478, 688 ),
        new CharMetrics( 500, 0, 16, -14, 468, 688 ), new CharMetrics( 500, 0, 19, 0, 475, 688 ),
        new CharMetrics( 500, 0, 22, -8, 470, 676 ), new CharMetrics( 500, 0, 28, -13, 475, 688 ),
        new CharMetrics( 500, 0, 17, 0, 477, 676 ), new CharMetrics( 500, 0, 28, -13, 472, 688 ),
        new CharMetrics( 500, 0, 26, -13, 473, 688 ), new CharMetrics( 333, 0, 82, -13, 251, 472 ),
        new CharMetrics( 333, 0, 82, -180, 266, 472 ), new CharMetrics( 570, 0, 31, -8, 539, 514 ),
        new CharMetrics( 570, 0, 33, 107, 537, 399 ), new CharMetrics( 570, 0, 31, -8, 539, 514 ),
        new CharMetrics( 500, 0, 57, -13, 445, 689 ), new CharMetrics( 930, 0, 108, -19, 822, 691 ),
        new CharMetrics( 722, 0, 9, 0, 689, 690 ), new CharMetrics( 667, 0, 16, 0, 619, 676 ),
        new CharMetrics( 722, 0, 49, -19, 687, 691 ), new CharMetrics( 722, 0, 14, 0, 690, 676 ),
        new CharMetrics( 667, 0, 16, 0, 641, 676 ), new CharMetrics( 611, 0, 16, 0, 583, 676 ),
        new CharMetrics( 778, 0, 37, -19, 755, 691 ), new CharMetrics( 778, 0, 21, 0, 759, 676 ),
        new CharMetrics( 389, 0, 20, 0, 370, 676 ), new CharMetrics( 500, 0, 3, -96, 479, 676 ),
        new CharMetrics( 778, 0, 30, 0, 769, 676 ), new CharMetrics( 667, 0, 19, 0, 638, 676 ),
        new CharMetrics( 944, 0, 14, 0, 921, 676 ), new CharMetrics( 722, 0, 16, -18, 701, 676 ),
        new CharMetrics( 778, 0, 35, -19, 743, 691 ), new CharMetrics( 611, 0, 16, 0, 600, 676 ),
        new CharMetrics( 778, 0, 35, -176, 743, 691 ), new CharMetrics( 722, 0, 26, 0, 715, 676 ),
        new CharMetrics( 556, 0, 35, -19, 513, 692 ), new CharMetrics( 667, 0, 31, 0, 636, 676 ),
        new CharMetrics( 722, 0, 16, -19, 701, 676 ), new CharMetrics( 722, 0, 16, -18, 701, 676 ),
        new CharMetrics( 1000, 0, 19, -15, 981, 676 ), new CharMetrics( 722, 0, 16, 0, 699, 676 ),
        new CharMetrics( 722, 0, 15, 0, 699, 676 ), new CharMetrics( 667, 0, 28, 0, 634, 676 ),
        new CharMetrics( 333, 0, 67, -149, 301, 678 ), new CharMetrics( 278, 0, -25, -19, 303, 691 ),
        new CharMetrics( 333, 0, 32, -149, 266, 678 ), new CharMetrics( 581, 0, 73, 311, 509, 676 ),
        new CharMetrics( 500, 0, 0, -125, 500, -75 ), new CharMetrics( 333, 0, 70, 356, 254, 691 ),
        new CharMetrics( 500, 0, 25, -14, 488, 473 ), new CharMetrics( 556, 0, 17, -14, 521, 676 ),
        new CharMetrics( 444, 0, 25, -14, 430, 473 ), new CharMetrics( 556, 0, 25, -14, 534, 676 ),
        new CharMetrics( 444, 0, 25, -14, 426, 473 ), new CharMetrics( 333, 0, 14, 0, 389, 691 ),
        new CharMetrics( 500, 0, 28, -206, 483, 473 ), new CharMetrics( 556, 0, 16, 0, 534, 676 ),
        new CharMetrics( 278, 0, 16, 0, 255, 691 ), new CharMetrics( 333, 0, -57, -203, 263, 691 ),
        new CharMetrics( 556, 0, 22, 0, 543, 676 ), new CharMetrics( 278, 0, 16, 0, 255, 676 ),
        new CharMetrics( 833, 0, 16, 0, 814, 473 ), new CharMetrics( 556, 0, 21, 0, 539, 473 ),
        new CharMetrics( 500, 0, 25, -14, 476, 473 ), new CharMetrics( 556, 0, 19, -205, 524, 473 ),
        new CharMetrics( 556, 0, 34, -205, 536, 473 ), new CharMetrics( 444, 0, 29, 0, 434, 473 ),
        new CharMetrics( 389, 0, 25, -14, 361, 473 ), new CharMetrics( 333, 0, 20, -12, 332, 630 ),
        new CharMetrics( 556, 0, 16, -14, 537, 461 ), new CharMetrics( 500, 0, 21, -14, 485, 461 ),
        new CharMetrics( 722, 0, 23, -14, 707, 461 ), new CharMetrics( 500, 0, 12, 0, 484, 461 ),
        new CharMetrics( 500, 0, 16, -205, 480, 461 ), new CharMetrics( 444, 0, 21, 0, 420, 461 ),
        new CharMetrics( 394, 0, 22, -175, 340, 698 ), new CharMetrics( 220, 0, 66, -19, 154, 691 ),
        new CharMetrics( 394, 0, 54, -175, 372, 698 ), new CharMetrics( 520, 0, 29, 173, 491, 333 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 278, 0, 16, 0, 255, 461 ),
        new CharMetrics( 333, 0, 8, 528, 246, 713 ), new CharMetrics( 333, 0, 86, 528, 324, 713 ),
        new CharMetrics( 333, 0, -2, 528, 335, 704 ), new CharMetrics( 333, 0, -16, 547, 349, 674 ),
        new CharMetrics( 333, 0, 1, 565, 331, 637 ), new CharMetrics( 333, 0, 15, 528, 318, 691 ),
        new CharMetrics( 333, 0, 103, 537, 230, 667 ), new CharMetrics( 333, 0, -2, 537, 335, 667 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 333, 0, 60, 527, 273, 740 ),
        new CharMetrics( 333, 0, 68, -218, 294, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 333, 0, -13, 528, 425, 713 ), new CharMetrics( 333, 0, 90, -173, 319, 44 ),
        new CharMetrics( 333, 0, -2, 528, 335, 704 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 333, 0, 82, -203, 252, 501 ), new CharMetrics( 500, 0, 53, -140, 458, 588 ),
        new CharMetrics( 500, 0, 21, -14, 477, 684 ), new CharMetrics( 500, 0, -26, 61, 526, 613 ),
        new CharMetrics( 500, 0, -64, 0, 547, 676 ), new CharMetrics( 220, 0, 66, -19, 154, 691 ),
        new CharMetrics( 500, 0, 57, -132, 443, 691 ), new CharMetrics( 333, 0, -2, 537, 335, 667 ),
        new CharMetrics( 747, 0, 26, -19, 721, 691 ), new CharMetrics( 300, 0, -1, 397, 301, 688 ),
        new CharMetrics( 500, 0, 23, 36, 473, 415 ), new CharMetrics( 570, 0, 33, 108, 537, 399 ),
        new CharMetrics( 333, 0, 44, 171, 287, 287 ), new CharMetrics( 747, 0, 26, -19, 721, 691 ),
        new CharMetrics( 333, 0, 1, 565, 331, 637 ), new CharMetrics( 400, 0, 57, 402, 343, 688 ),
        new CharMetrics( 570, 0, 33, 0, 537, 506 ), new CharMetrics( 300, 0, 0, 275, 300, 688 ),
        new CharMetrics( 300, 0, 3, 268, 297, 688 ), new CharMetrics( 333, 0, 86, 528, 324, 713 ),
        new CharMetrics( 556, 0, 33, -206, 536, 461 ), new CharMetrics( 540, 0, 0, -186, 519, 676 ),
        new CharMetrics( 250, 0, 41, 248, 210, 417 ), new CharMetrics( 333, 0, 68, -218, 294, 0 ),
        new CharMetrics( 300, 0, 28, 275, 273, 688 ), new CharMetrics( 330, 0, 18, 397, 312, 688 ),
        new CharMetrics( 500, 0, 27, 36, 477, 415 ), new CharMetrics( 750, 0, 28, -12, 743, 688 ),
        new CharMetrics( 750, 0, -7, -12, 775, 688 ), new CharMetrics( 750, 0, 23, -12, 733, 688 ),
        new CharMetrics( 500, 0, 55, -201, 443, 501 ), new CharMetrics( 722, 0, 9, 0, 689, 923 ),
        new CharMetrics( 722, 0, 9, 0, 689, 923 ), new CharMetrics( 722, 0, 9, 0, 689, 914 ),
        new CharMetrics( 722, 0, 9, 0, 689, 884 ), new CharMetrics( 722, 0, 9, 0, 689, 877 ),
        new CharMetrics( 722, 0, 9, 0, 689, 935 ), new CharMetrics( 1000, 0, 4, 0, 951, 676 ),
        new CharMetrics( 722, 0, 49, -218, 687, 691 ), new CharMetrics( 667, 0, 16, 0, 641, 923 ),
        new CharMetrics( 667, 0, 16, 0, 641, 923 ), new CharMetrics( 667, 0, 16, 0, 641, 914 ),
        new CharMetrics( 667, 0, 16, 0, 641, 877 ), new CharMetrics( 389, 0, 20, 0, 370, 923 ),
        new CharMetrics( 389, 0, 20, 0, 370, 923 ), new CharMetrics( 389, 0, 20, 0, 370, 914 ),
        new CharMetrics( 389, 0, 20, 0, 370, 877 ), new CharMetrics( 722, 0, 6, 0, 690, 676 ),
        new CharMetrics( 722, 0, 16, -18, 701, 884 ), new CharMetrics( 778, 0, 35, -19, 743, 923 ),
        new CharMetrics( 778, 0, 35, -19, 743, 923 ), new CharMetrics( 778, 0, 35, -19, 743, 914 ),
        new CharMetrics( 778, 0, 35, -19, 743, 884 ), new CharMetrics( 778, 0, 35, -19, 743, 877 ),
        new CharMetrics( 570, 0, 48, 16, 522, 490 ), new CharMetrics( 778, 0, 35, -74, 743, 737 ),
        new CharMetrics( 722, 0, 16, -19, 701, 923 ), new CharMetrics( 722, 0, 16, -19, 701, 923 ),
        new CharMetrics( 722, 0, 16, -19, 701, 914 ), new CharMetrics( 722, 0, 16, -19, 701, 877 ),
        new CharMetrics( 722, 0, 15, 0, 699, 928 ), new CharMetrics( 611, 0, 16, 0, 600, 676 ),
        new CharMetrics( 556, 0, 19, -12, 517, 691 ), new CharMetrics( 500, 0, 25, -14, 488, 713 ),
        new CharMetrics( 500, 0, 25, -14, 488, 713 ), new CharMetrics( 500, 0, 25, -14, 488, 704 ),
        new CharMetrics( 500, 0, 25, -14, 488, 674 ), new CharMetrics( 500, 0, 25, -14, 488, 667 ),
        new CharMetrics( 500, 0, 25, -14, 488, 740 ), new CharMetrics( 722, 0, 33, -14, 693, 473 ),
        new CharMetrics( 444, 0, 25, -218, 430, 473 ), new CharMetrics( 444, 0, 25, -14, 426, 713 ),
        new CharMetrics( 444, 0, 25, -14, 426, 713 ), new CharMetrics( 444, 0, 25, -14, 426, 704 ),
        new CharMetrics( 444, 0, 25, -14, 426, 667 ), new CharMetrics( 278, 0, -26, 0, 255, 713 ),
        new CharMetrics( 278, 0, 16, 0, 290, 713 ), new CharMetrics( 278, 0, -36, 0, 301, 704 ),
        new CharMetrics( 278, 0, -36, 0, 301, 667 ), new CharMetrics( 500, 0, 25, -14, 476, 691 ),
        new CharMetrics( 556, 0, 21, 0, 539, 674 ), new CharMetrics( 500, 0, 25, -14, 476, 713 ),
        new CharMetrics( 500, 0, 25, -14, 476, 713 ), new CharMetrics( 500, 0, 25, -14, 476, 704 ),
        new CharMetrics( 500, 0, 25, -14, 476, 674 ), new CharMetrics( 500, 0, 25, -14, 476, 667 ),
        new CharMetrics( 570, 0, 33, -31, 537, 537 ), new CharMetrics( 500, 0, 25, -92, 476, 549 ),
        new CharMetrics( 556, 0, 16, -14, 537, 713 ), new CharMetrics( 556, 0, 16, -14, 537, 713 ),
        new CharMetrics( 556, 0, 16, -14, 537, 704 ), new CharMetrics( 556, 0, 16, -14, 537, 667 ),
        new CharMetrics( 500, 0, 16, -205, 480, 713 ), new CharMetrics( 556, 0, 19, -205, 524, 676 ),
        new CharMetrics( 500, 0, 16, -205, 480, 667 )};

    SerifBold()
    {
        super( false, 676, -205, new CharMetrics( 0, 0, -168, -218, 1000, 935 ), metrics );
    }
}
